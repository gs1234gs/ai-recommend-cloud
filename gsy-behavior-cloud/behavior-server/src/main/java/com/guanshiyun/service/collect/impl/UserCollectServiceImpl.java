package com.guanshiyun.service.collect.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.collect.UserCollectMongodb;
import com.guanshiyun.controller.collect.vo.UserCollectSaveVO;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.repository.collect.UserCollectMongodbRepository;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.rpc.profile.CategoryApiVO;
import com.guanshiyun.rpc.profile.SKUApiVO;
import com.guanshiyun.rpc.profile.TagApiVO;
import com.guanshiyun.service.collect.UserCollectService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCollectServiceImpl implements UserCollectService {
    private final UserCollectMongodbRepository userCollectMongodbRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final SnowflakePermanent snowflakePermanent;
    private final GorseClient gorseClient;
    private final CategoryApiService categoryApiService;
    private final SkuApiService skuApiService;
    private final TagApiService tagApiService;
    private final MyBigInteger myBigInteger;

    /**
     *
     * @param userCollectSaveVO
     * @return BigInteger
     * @throws RuntimeException
     * @author guanshiyun
     * @date 2025/12/19 15:06
     * 保存收藏记录
     *
     * */
    @Override
    public Mono<BigInteger> save(UserCollectSaveVO userCollectSaveVO) {
        return Mono.deferContextual(ctx -> {
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Mono.error(new RuntimeException("用户ID不能为空"));
            }
            UserCollectMongodb userCollectMongodb = BeanUtil.toBean(userCollectSaveVO, UserCollectMongodb.class);
            BigInteger id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);

            userCollectMongodb
                    .setId( id)
                    .setCreateTime(now)
                    .setCreator(userId);
            BigInteger productId = userCollectMongodb.getProduct().getId();
            Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                    categoryApiService.findByProductId(productId);
            Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                    skuApiService.findByProductId(productId);
            Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                    tagApiService.findByProductId(productId);
            return Mono.zip(categoryApiServiceByProductId, skuApiServiceByProductId, tagApiServiceByProductId)
                    .map(tuple -> {
                                UserCollectMongodb clickMongodb = userCollectMongodb.setSkuList(tuple.getT2().getData())
                                        .setCategoryList(tuple.getT1().getData())
                                        .setTagList(tuple.getT3().getData());
                                Feedback feedback = Feedback.builder()
                                        .feedbackType(GorseFeedbackEnum.COLLECT.getValue())
                                        .userId(userId.toString())
                                        .itemId(productId.toString())
                                        .timestamp(userCollectMongodb.getCollectTime().format(DateTimeFormatter.ISO_DATE_TIME))
                                        .build();
                                return Tuples.of(clickMongodb, feedback);
                            }

                    )
                    .flatMap(tuple -> {
                        UserCollectMongodb collectMongodb = tuple.getT1();
                        Feedback feedback = tuple.getT2();
                        Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(List.of(feedback));
                        Mono<UserCollectMongodb> save = userCollectMongodbRepository.save(collectMongodb);
                        return Mono.zip(rowAffectedMono, save)
                                .map(t -> t.getT2().getId());
                    });

    });
    }

    /**
     *
     * @param rows
     * @return Flux<UserCollectVO>
     * @throws RuntimeException
     * @author guanshiyun
     * @date 2025/12/19 15:06
     * 查询用户收藏记录
     *
     * */
    @Override
    public Flux<UserCollectVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Flux.empty();
            }

            int limit = (Objects.isNull( rows) || rows <= ConstNumber.INT_ZERO) ? ConstNumber.INTEGER_TEN : rows;
            BigInteger userId =
                    myBigInteger
                            .bigIntegerOrNull(
                                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));

            Query query = new Query()
                    .with(Sort.by(Sort.Order.desc(UserCollectMongodb.Fields.collectTime))) // 最近记录在前
                    .limit(limit)
                    .addCriteria(Criteria.where(BasePojo.Fields.creator).is(userId));

            return reactiveMongoTemplate.find(query, UserCollectMongodb.class)
                    .map(item -> BeanConvertUtil.toBean(item, UserCollectVO.class))
                    .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
        });
    }

    @Override
    public Mono<Void> deleteById(BigInteger id) {
        return userCollectMongodbRepository.deleteById(id);
    }

}
