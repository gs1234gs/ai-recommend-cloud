package com.guanshiyun.repository.sysuser.impl;

import com.guanshiyun.repository.sysuser.SysUserRepository;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public abstract class SysUserRepositoryImpl implements SysUserRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> deleteUserById(Long id){
        return databaseClient.sql("delete from sys_user where id = :id")
                .bind(SysUser.Fields.id, id)
                .fetch()
                .rowsUpdated();
    }



}
