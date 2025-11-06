package com.guanshiyun.repository.userrole;

import com.guanshiyun.relationpojo.SysUserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface SysUserRoleRepository extends ReactiveCrudRepository<SysUserRole, BigInteger> {
    @Query("select role_id from sys_user_role where user_id = :userId")
    Flux<BigInteger> findRoleIdByUserId(BigInteger userId);
    @Query("SELECT COUNT(*) FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId")
    Mono<BigInteger> findExistsUserRole(@Param("userId") BigInteger userId, @Param("roleId") BigInteger roleId);
    //根据用户id删除用户角色关联
    @Query("DELETE FROM sys_user_role WHERE user_id = :id")
    Mono<Void> deleteAllByUserId(BigInteger id);
}
