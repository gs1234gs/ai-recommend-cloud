package com.guanshiyun.service.search.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.search.vo.UserSearchSaveVO;
import com.guanshiyun.controller.search.vo.UserSearchVO;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.repository.search.UserSearchMongodbRepository;
import com.guanshiyun.search.UserSearchMongodb;
import com.guanshiyun.service.search.UserSearchService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {
    private final UserSearchMongodbRepository userSearchMongodbRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final SnowflakePermanent snowflakePermanent;
    private final GorseClient gorseClient;
    private final MyBigInteger myBigInteger;

    /**
     * @param userSearchVO
     * @return BigInteger
     * 保存用户搜索记录
     */
    @Override
    public Mono<BigInteger> save(UserSearchSaveVO userSearchVO) {

        return Mono.deferContextual(ctx -> {
            UserSearchMongodb userSearchMongodb =
                    BeanUtil.toBean(userSearchVO, UserSearchMongodb.class);
            BigInteger id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            userSearchMongodb.setId(id).setCreateTime(now);
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger creator = myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                userSearchMongodb.setCreator(creator);
            }

            return userSearchMongodbRepository.save(userSearchMongodb)
//            Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(List.of(
//                    Feedback.builder()
//                            .userId(
//                                    Objects.nonNull(userSearchMongodb.getCreator())
//                                            ?
//                                            userSearchMongodb.getCreator().toString()
//                                            :
//                                            GuestEnum.GUEST_USER_ID.getValue()
//                            )
//                            .itemId(userSearchMongodb.getId().toString())
//                            .timestamp(userSearchMongodb.getCreateTime().format(DateTimeFormatter.ISO_DATE_TIME))
//                            .feedbackType(GorseFeedbackEnum.SEARCH.getValue())
//                            .build()
//            ));
//            return Mono.zip(save, rowAffectedMono)
                    .map(UserSearchMongodb::getId)
                    .onErrorResume(e ->
                            {
                                log.error("错误", e);
                                return Mono.error(new RuntimeException("保存失败", e));
                            }
                    );
        });
    }

    /**
     * @param rows
     * @return Flux<UserSearchVO>
     * 查询用户搜索记录
     */
    @Override
    public Flux<UserSearchVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Flux.empty();
            }

            int limit = (Objects.isNull(rows) || rows <= ConstNumber.INT_ZERO) ? ConstNumber.INTEGER_TEN : rows;
            BigInteger userId =
                    myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));

            Query query = new Query()
                    .with(Sort.by(Sort.Order.desc(BasePojo.Fields.createTime))) // 最近搜索在前
                    .limit(limit)
                    .addCriteria(Criteria.where(BasePojo.Fields.creator).is(userId));

            return reactiveMongoTemplate.find(query, UserSearchMongodb.class)
                    .map(item -> BeanUtil.toBean(item, UserSearchVO.class))
                    .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
        });
    }


}
