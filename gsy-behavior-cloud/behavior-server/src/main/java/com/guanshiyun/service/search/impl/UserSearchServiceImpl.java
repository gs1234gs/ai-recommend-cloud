package com.guanshiyun.service.search.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.search.vo.UserSearchVO;
import com.guanshiyun.repository.search.UserSearchMongodbRepository;
import com.guanshiyun.repository.search.UserSearchRepository;
import com.guanshiyun.search.UserSearch;
import com.guanshiyun.search.UserSearchMongodb;
import com.guanshiyun.service.search.UserSearchService;
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
public class UserSearchServiceImpl implements UserSearchService {
    private final UserSearchMongodbRepository userSearchMongodbRepository;
    private final UserSearchRepository userSearchRepository;
    private final SnowflakePermanent snowflakePermanent;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final MyBigInteger myBigInteger;

    @Override
    public Mono<BigInteger> save(UserSearchVO userSearchVO) {
        return Mono.deferContextual(ctx ->{
            UserSearchMongodb userSearchMongodb =
                    BeanUtil.toBean(userSearchVO, UserSearchMongodb.class);
            BigInteger id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            userSearchMongodb.setCreateTime(now);
            userSearchMongodb.setId(id);
            userSearchMongodb.getSearchContent().forEach(searchContent ->
                    searchContent.setId(snowflakePermanent.nextId()));
            if(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                BigInteger useId =
                        myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                UserSearch userSearch = UserSearch.builder()
                        .id(id)
                        .createTime(now)
                        .creator(useId)
                        .build();
               return r2dbcEntityTemplate.insert(userSearch)
                        .flatMap(save -> {
                            log.info("保存成功: {}", save);
                            userSearchMongodb.setCreator(useId);
                            return userSearchMongodbRepository.save(userSearchMongodb)
                                    .map(saveM->save.getId());
                        });
            }
            UserSearch userSearch = UserSearch.builder()
                    .id(id)
                    .updateTime(now)
                    .build();
            return r2dbcEntityTemplate.insert(userSearch)
                    .flatMap(save -> {
                        log.info("保存成功: {}", save);
                        return userSearchMongodbRepository.save(userSearchMongodb)
                                .map(saveM->save.getId());
                    });
        });
    }

    @Override
    public Flux<UserSearchVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {
            Integer row = Objects.isNull(rows) ? ConstNumber.INTEGER_TEN : rows;
            if(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                BigInteger useId =
                        myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return userSearchRepository.findAll(row, useId)
                        .flatMap(userSearch -> {
                            return userSearchMongodbRepository.findById(userSearch.getId())
                                    .map(userSearchMongodb ->
                                            BeanUtil.toBean(userSearchMongodb, UserSearchVO.class))
                                    .onErrorResume(e->Mono.error(new RuntimeException("查询失败", e)));
                        });
            }
            return userSearchRepository.findAll(row)
                    .flatMap(userSearch -> {
                        return userSearchMongodbRepository.findById(userSearch.getId())
                                .map(userSearchMongodb ->
                                        BeanUtil.toBean(userSearchMongodb, UserSearchVO.class))
                                .onErrorResume(e->Mono.error(new RuntimeException("查询失败", e)));
                    });
        });
    }
}
