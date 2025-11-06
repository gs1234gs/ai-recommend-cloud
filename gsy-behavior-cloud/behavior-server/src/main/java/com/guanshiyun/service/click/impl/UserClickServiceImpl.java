package com.guanshiyun.service.click.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.click.UserClick;
import com.guanshiyun.click.UserClickMongodb;
import com.guanshiyun.controller.click.vo.UserClickVO;
import com.guanshiyun.profile.ClickProfile;
import com.guanshiyun.repository.click.UserClickMongodbRepository;
import com.guanshiyun.repository.click.UserClickRepository;
import com.guanshiyun.service.click.UserClickService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClickServiceImpl implements UserClickService {
    private final UserClickMongodbRepository userClickMongodbRepository;
    private final UserClickRepository userClickRepository;
    private final MyBigInteger myBigInteger;
    private final SnowflakePermanent snowflakePermanent;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * 保存点击记录
     */
    @Override
    public Mono<BigInteger> save(UserClickVO userClickVO) {
        return Mono.deferContextual(ctx -> {
                    UserClickMongodb userClickMongodb = BeanUtil.toBean(userClickVO, UserClickMongodb.class);
                    LocalDateTime createTime = LocalDateTime.now();
                    userClickMongodb.setCreateTime(createTime);
                    List<ClickProfile> clickContent = userClickMongodb.getClickContent();
                    clickContent.forEach(clickProfile -> clickProfile.setId(snowflakePermanent.nextId()));
                    BigInteger id = snowflakePermanent.nextId();
                    userClickMongodb.setId(id);
                    if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                        BigInteger useId =
                                myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                        UserClick userClick = UserClick.builder()
                                .id(id)
                                .createTime(createTime)
                                .creator(useId)
                                .build();
                        userClickMongodb.setCreator(useId);
                        return r2dbcEntityTemplate.insert(userClick)
                                .flatMap(save -> userClickMongodbRepository.save(userClickMongodb)
                                        .then(Mono.just(save.getId())))
                                .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
                    }
                    UserClick userClick = UserClick.builder()
                            .id(id)
                            .createTime(createTime)
                            .creator(null)
                            .build();
                    userClickMongodb.setCreator(null);
                    return r2dbcEntityTemplate.insert(userClick)
                            .flatMap(save -> userClickMongodbRepository.save(userClickMongodb)
                                    .then(Mono.just(save.getId())))
                            .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));

                })
                .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
    }

    @Override
    public Flux<UserClickVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {
            Integer row = Objects.isNull(rows) ? ConstNumber.INTEGER_TEN : rows;
            if(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                BigInteger useId =
                        myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
               return userClickRepository.findAll(row, useId)
                       .flatMap(userClickVO -> {
                           return userClickMongodbRepository.findById(userClickVO.getId())
                                   .map(userClickMongodb ->
                                           BeanUtil.toBean(userClickMongodb, UserClickVO.class));
                       })
                       .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
            }
            return userClickRepository.findAll(row)
                    .flatMap(userClick -> {
                        return userClickMongodbRepository.findById(userClick.getId())
                                .map(userClickMongodb ->
                                        BeanUtil.toBean(userClickMongodb, UserClickVO.class));
                    })
                    .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
        });
    }
}
