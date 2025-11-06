package com.guanshiyun.repository.userrole;

import com.guanshiyun.relationpojo.SysUserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface SysUserRoleRepository extends ReactiveCrudRepository<SysUserRole, BigInteger> {

    @Query("SELECT EXISTS(SELECT 1 FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId)")
    Mono<Long> existsByUserIdAndRoleId(BigInteger userId, BigInteger roleId);


    @Query("select role_id from sys_user_role where user_id = :userId")
    Flux<BigInteger> findRoleIdByUserId(BigInteger userId);
}
