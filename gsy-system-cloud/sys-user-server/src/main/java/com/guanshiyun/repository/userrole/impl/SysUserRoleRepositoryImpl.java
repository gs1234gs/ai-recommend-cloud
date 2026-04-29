package com.guanshiyun.repository.userrole.impl;

import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public abstract class SysUserRoleRepositoryImpl implements SysUserRoleRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> deleteSysUserRoleByUserId(Long userId) {
       return databaseClient.sql("delete from sys_user_role where user_id = :id")
                .bind(SysUserRole.Fields.id, userId)
                .fetch()
                .rowsUpdated();
    }
}
