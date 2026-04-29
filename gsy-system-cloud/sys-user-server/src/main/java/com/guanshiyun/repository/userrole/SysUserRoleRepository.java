package com.guanshiyun.repository.userrole;

import com.guanshiyun.relationpojo.SysUserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface SysUserRoleRepository extends R2dbcRepository<SysUserRole, Long> {
    @Query("select role_id from sys_user_role where user_id = :userId")
    Flux<Long> findRoleIdByUserId(Long userId);
    @Query("SELECT COUNT(*) FROM sys_user_role WHERE user_id = :userId AND role_id in :roleIds")
    Mono<Long> findExistsUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    //根据用户id删除用户角色关联
    @Query("DELETE FROM sys_user_role WHERE user_id = :userId")
    Mono<Void> deleteAllByUserId(Long userId);

    Flux<SysUserRole> findSysUserRoleByUserId(Long userId);

    Mono<Void> deleteByUserIdAndRoleIdIn(Long userId, List<Long> toDeleteIds);

    Mono<Long> deleteSysUserRoleByUserId(Long userId);

}
