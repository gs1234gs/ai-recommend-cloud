package com.guanshiyun.service.collect.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.click.UserClick;
import com.guanshiyun.collect.UserCollectMongodb;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.repository.collect.UserCollectMongodbRepository;
import com.guanshiyun.repository.collect.UserCollectRepository;
import com.guanshiyun.service.collect.UserCollectService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCollectServiceImpl implements UserCollectService {
    private final UserCollectMongodbRepository userCollectMongodbRepository;
    private final UserCollectRepository userCollectRepository;
    private final MyBigInteger myBigInteger;
    private final SnowflakePermanent snowflakePermanent;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<BigInteger> save(UserCollectVO userCollectVO) {
        return Mono.deferContextual(ctx -> {
            UserCollectMongodb userCollectMongodb = BeanUtil.toBean(userCollectVO, UserCollectMongodb.class);
            BigInteger id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            userCollectMongodb.setCreateTime(now);
            userCollectMongodb.setId( id);
            userCollectMongodb.getCollectContent().forEach(collectProfile -> collectProfile.setId(snowflakePermanent.nextId()));
            if(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                BigInteger useId =
                        myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                UserClick userClick = UserClick.builder()
                        .createTime(now)
                        .creator(useId)
                        .id(id)
                        .build();
                userCollectMongodb.setCreator(useId);
                return r2dbcEntityTemplate.insert(userClick)
                        .flatMap(save -> userCollectMongodbRepository.save(userCollectMongodb)
                                .then(Mono.just(save.getId())))
                        .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
            }
            return r2dbcEntityTemplate.insert(userCollectMongodb)
                    .flatMap(save->{
                        return userCollectMongodbRepository.save(userCollectMongodb)
                                .then(Mono.just(save.getId()));
                    })
                    .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
        }).onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
    }

    @Override
    public Flux<UserCollectVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {
            Integer row = Objects.isNull(rows) ? ConstNumber.INTEGER_TEN : rows;
            if(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                BigInteger useId =
                        myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return userCollectRepository.findAll(  row, useId)
                        .flatMap(userCollect -> {
                           return userCollectMongodbRepository.findById(userCollect.getId())
                                    .map(userCollectMongodb ->
                                            BeanUtil.toBean(userCollectMongodb, UserCollectVO.class));
                        });
            }
            return userCollectRepository.findAll( row)
                    .flatMap(userCollect -> {
                        return userCollectMongodbRepository.findById(userCollect.getId())
                                .map(userCollectMongodb ->
                                        BeanUtil.toBean(userCollectMongodb, UserCollectVO.class));
                    })
                    .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
        });
    }
}
