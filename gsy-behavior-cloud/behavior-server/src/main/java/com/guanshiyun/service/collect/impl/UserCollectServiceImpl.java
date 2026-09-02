package com.guanshiyun.service.collect.impl;

import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.collect.UserCollectMongodb;
import com.guanshiyun.controller.collect.vo.UserCollectSaveVO;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import com.guanshiyun.repository.collect.UserCollectMongodbRepository;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.service.collect.UserCollectService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

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
    private final MyLong myLong;

    /**
     *
     * @param userCollectSaveVO
     * @return Long
     * @throws RuntimeException
     * @author guanshiyun
     * @date 2025/12/19 15:06
     * 保存收藏记录
     *
     * */
    @Override
    public Mono<Long> save(UserCollectSaveVO userCollectSaveVO) {
        return Mono.deferContextual(ctx -> {
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Mono.error(new RuntimeException("用户ID不能为空"));
            }

            UserCollectMongodb userCollectMongodb = BeanConvertUtil.toBean(userCollectSaveVO, UserCollectMongodb.class);
            Long id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            Long userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);

            userCollectMongodb
                    .setId( id)
                    .setCreateTime(now)
                    .setCreator(userId);
            Long productId = userCollectMongodb.getProduct().getId();
            if(Objects.isNull(productId)){
                return Mono.just(id);
            }
            return userCollectMongodbRepository.findByProductIdAndCreator(productId,userId)
                    .hasElements()
                    .flatMap(flag->{
                        if(flag){
                            return Mono.just(id);
                        }
                        Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                                categoryApiService.findByProductId(productId);
                        Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                                skuApiService.findByProductId(productId);
                        Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                                tagApiService.findByProductId(productId);
                        return Mono.zip(categoryApiServiceByProductId, skuApiServiceByProductId, tagApiServiceByProductId)
                                .map(tuple -> {
                                            UserCollectMongodb collectMongodb = userCollectMongodb.setSkuList(tuple.getT2().getData())
                                                    .setCategoryList(tuple.getT1().getData())
                                                    .setTagList(tuple.getT3().getData());
                                            Feedback feedback = Feedback.builder()
                                                    .feedbackType(GorseFeedbackEnum.COLLECT.getValue())
                                                    .userId(userId.toString())
                                                    .itemId(productId.toString())
                                                    .timestamp(userCollectMongodb.getCollectTime().format(DateTimeFormatter.ISO_DATE_TIME))
                                                    .build();
                                            return Tuples.of(collectMongodb, feedback);
                                        }

                                )
                                .flatMap(tuple -> {
                                    UserCollectMongodb collectMongodb = tuple.getT1();
                                    Feedback feedback = tuple.getT2();
                                    Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(List.of(feedback));
                                    Mono<UserCollectMongodb> save = userCollectMongodbRepository.save(collectMongodb);
                                    return Mono.zip(rowAffectedMono, save)
                                            .map(t -> {
                                                log.info("保存收藏记录成功{}:{}", t.getT1().getRowAffected(), t.getT2().getId());
                                                return t.getT2().getId();
                                            })
                                            .onErrorResume(e -> {
                                                log.error("保存收藏记录失败", e);
                                                return Mono.error(new RuntimeException("保存收藏记录失败", e));
                                            });
                                })
                                .onErrorResume(e->{
                                    log.error("保存收藏记录失败", e);
                                    return Mono.error(new RuntimeException("保存收藏记录失败", e));
                                });

                    });
                    })
                .onErrorResume(e->{
                    log.error("保存click ： ",e);
                    return Mono.error(new Throwable("保存失败", e));
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
            Long userId =
                    myLong
                            .longOrNull(
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
    public Mono<Void> deleteById(Long id) {
        return userCollectMongodbRepository.deleteById(id);
    }

    @Override
    public Mono<PageResultT<List<UserCollectVO>>> findByPage(Integer pageNum, Integer pageSize) {
        // 1. 参数校验
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        Integer finalPageNum = pageNum;
        Integer finalPageSize = pageSize;
        return Mono.deferContextual(ctx->{
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.empty();
            }
            Long userId = myLong.longOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            // 2. 创建 Pageable (页码从 0 开始)
            Pageable pageable = PageRequest.of(
                    finalPageNum - 1,
                    finalPageSize,
                    Sort.by(BasePojo.Fields.createTime)
                            .descending()
            );

            // 3. 构建两个 Mono
            // A. 查询列表
            Mono<List<UserCollectVO>> listMono = userCollectMongodbRepository
                    .findByCreator(userId, pageable) // 传入 creatorId 和 pageable
                    .map(entity -> BeanConvertUtil.toBean(entity, UserCollectVO.class))   // 转 VO
                    .collectList();                       // 转 List

            // B. 查询总数 (确保条件一致)
            Mono<Long> totalMono = userCollectMongodbRepository.countByCreator(userId);

            // 4. 并行执行并组装
            return Mono.zip(listMono, totalMono)
                    .map(tuple ->
                            PageResultT
                            .<List<UserCollectVO>>builder()
                            .rows(tuple.getT1())
                            .total(tuple.getT2())
                            .build()
                    );
        });


    }

}
