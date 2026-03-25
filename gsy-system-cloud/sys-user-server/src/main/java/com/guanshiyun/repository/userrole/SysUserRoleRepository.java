package com.guanshiyun.repository.userrole;

import com.guanshiyun.relationpojo.SysUserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface SysUserRoleRepository extends ReactiveCrudRepository<SysUserRole, Long> {
    @Query("select role_id from sys_user_role where user_id = :userId")
    Flux<Long> findRoleIdByUserId(Long userId);
    @Query("SELECT COUNT(*) FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId")
    Mono<Long> findExistsUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    //根据用户id删除用户角色关联
    @Query("DELETE FROM sys_user_role WHERE user_id = :id")
    Mono<Void> deleteAllByUserId(Long id);
}
