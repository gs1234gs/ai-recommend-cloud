package com.guanshiyun.service.browse.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.CursorQuery;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.browse.UserBrowse;
import com.guanshiyun.browse.UserBrowseMongodb;
import com.guanshiyun.controller.browse.vo.UserBrowseVO;
import com.guanshiyun.profile.BrowseProfile;
import com.guanshiyun.repository.browse.UserBrowseMongodbRepository;
import com.guanshiyun.repository.browse.UserBrowseRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.service.browse.UserBrowseService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
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
public class UserBrowseServiceImpl implements UserBrowseService {
    private final UserBrowseMongodbRepository userBrowseMongodbRepository;
    private final UserBrowseRepository userBrowseRepository;
    private final SnowflakePermanent snowflakePermanent;
    private final DatabaseClient databaseClient;
    private final MyBigInteger myBigInteger;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    /**
     * 保存浏览记录
     */
    @Override
    public Mono<BigInteger> save(UserBrowseVO userBrowseVO) {
        UserBrowseMongodb userBrowseMongodb = BeanUtil.toBean(userBrowseVO, UserBrowseMongodb.class);

        return Mono.deferContextual(ctx -> {
            LocalDateTime now = LocalDateTime.now();
            BigInteger id = snowflakePermanent.nextId();
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger useId =
                        myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                if (Objects.isNull(useId)) {
                    return Mono.error(new RuntimeException("用户不存在"));
                }
                UserBrowse userBrowse = UserBrowse.builder().id(id)
                        .createTime(now)
                        .creator(useId)
                        .build();
                return r2dbcEntityTemplate.insert(userBrowse)
                        .flatMap(save -> {
                            log.info("保存成功，id为{}", save.getId());
                            userBrowseMongodb.setDelFlag(save.delFlag);
                            userBrowseMongodb.setId(snowflakePermanent.nextId());
                            userBrowseMongodb.setCreateTime(save.getCreateTime());
                            userBrowseMongodb.setCreator(useId);
                            List<BrowseProfile> browseContent = userBrowseMongodb.getBrowseContent();
                            browseContent.forEach(browseProfile -> browseProfile.setId(snowflakePermanent.nextId()));
                            return userBrowseMongodbRepository.save(userBrowseMongodb)
                                    .map(UserBrowseMongodb::getId)
                                    .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
                        })
                        .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
            }
            //如果不存在，创建者id为null,表示游客
            UserBrowse userBrowse = UserBrowse.builder().id(id)
                    .createTime(now)
                    .creator(null)
                    .build();
            return r2dbcEntityTemplate.insert(userBrowse)
                    .flatMap(save -> {
                        userBrowseMongodb.setDelFlag(save.delFlag);
                        userBrowseMongodb.setId(snowflakePermanent.nextId());
                        userBrowseMongodb.setCreateTime(save.getCreateTime());
                        userBrowseMongodb.setCreator(null);
                        List<BrowseProfile> browseContent = userBrowseMongodb.getBrowseContent();
                        browseContent.forEach(browseProfile -> browseProfile.setId(snowflakePermanent.nextId()));
                        return userBrowseMongodbRepository.save(userBrowseMongodb)
                                .map(UserBrowseMongodb::getId)
                                .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
                    })
                    .onErrorResume(e -> Mono.error(new RuntimeException("保存失败", e)));
        });
    }

    @Override
    public Mono<BigInteger> update(UserBrowseVO userBrowseVO) {
        return Mono.deferContextual(ctx -> {
                    BigInteger useId =
                            myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                    if (Objects.isNull(useId)) {
                        return Mono.error(new RuntimeException("用户不存在"));
                    }
                    UserBrowseMongodb userBrowseMongodb = BeanUtil.toBean(userBrowseVO, UserBrowseMongodb.class);
                    LocalDateTime now = LocalDateTime.now();
                    return databaseClient.sql("update user_browse set update_time = :updateTime,updater = :updater where id = :id")
                            .bind(BasePojo.Fields.updateTime, now)
                            .bind(BasePojo.Fields.updater, useId)
                            .bind(UserBrowse.Fields.id, userBrowseVO.getId())
                            .fetch()
                            .rowsUpdated()
                            .flatMap(rowsUpdated -> {
                                if (rowsUpdated > 0) {
                                    log.info("更新成功，id为{}", userBrowseVO.getId());
                                    userBrowseMongodb.setUpdateTime(now);
                                    userBrowseMongodb.setUpdater(useId);
                                    List<BrowseProfile> browseContent = userBrowseMongodb.getBrowseContent();
                                    Query query = Query.query(
                                            Criteria
                                                    .where(UserBrowseMongodb.Fields.id)
                                                    .is(userBrowseVO.getId())
                                    );
                                    Update update = new Update().push(UserBrowseMongodb.Fields.browseContent, browseContent)
                                            .set(BasePojo.Fields.updateTime, now)
                                            .set(BasePojo.Fields.updater, useId);
                                    return reactiveMongoTemplate.updateFirst(
                                                    query, update,
                                                    UserBrowseMongodb.class)
                                            .flatMap(updateResult -> {
                                                log.info("更新成功，id为{}", userBrowseVO.getId());
                                                return Mono.just(userBrowseVO.getId());
                                            });
                                }
                                return Mono.just(userBrowseVO.getId());
                            });
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("更新失败", e)));
    }

    //获取浏览记录
    @Override
    public Flux<UserBrowseVO> findAll(Integer rows) {
        //设置动态参数，获取做多浏览记录
        return Flux.deferContextual(ctx -> {
            Integer row = Objects.isNull(rows) ? ConstNumber.INTEGER_TEN : rows;
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger useId =
                        myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                log.info("用户id为: {}", useId);
                return userBrowseRepository.findAll(row, useId)
                        .flatMap(userBrowse -> {
                            return userBrowseMongodbRepository.findById(userBrowse.getId())
                                    .map(userBrowseMongodb ->
                                            BeanUtil.toBean(userBrowseMongodb, UserBrowseVO.class)
                                    ).switchIfEmpty(Mono.empty())
                                    .onErrorResume(e -> Mono.error(new RuntimeException("mongodb查询失败", e)));
                        })
                        .switchIfEmpty(Mono.empty())
                        .onErrorResume(e -> Mono.error(new RuntimeException("查询失败", e)));
            }
            log.info("用户id为 null");
            return userBrowseRepository.findAll(row)
                    .flatMap(userBrowse -> {
                        return userBrowseMongodbRepository.findById(userBrowse.getId())
                                .map(userBrowseMongodb ->
                                        BeanUtil.toBean(userBrowseMongodb, UserBrowseVO.class)
                                )
                                .onErrorResume(e -> Mono.error(new RuntimeException("mongodb查询失败", e)))
                                .switchIfEmpty(Mono.empty());
                    })
                    .switchIfEmpty(Mono.empty())
                    .onErrorResume(e -> Mono.error(new RuntimeException("查询失败", e)));
        });
    }

    //游标分页
    @Override
    public Flux<UserBrowseVO> findAllByCursor(RequestCursorPage<UserBrowse> cursorPage) {
        UserBrowse userBrowse = cursorPage.getCondition();
        return CursorQuery.of(r2dbcEntityTemplate, UserBrowse.class, cursorPage)
                .list()
                .flatMap(userBrowses ->
                        userBrowseMongodbRepository.findById(userBrowse.getId())
                                .map(userBrowseMongodb ->
                                        BeanUtil.toBean(userBrowseMongodb, UserBrowseVO.class)
                                )
                );
    }
}
