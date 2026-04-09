package com.guanshiyun.repository.menurole;

import com.guanshiyun.relationpojo.SysRoleMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface SysRoleMenuRepository extends R2dbcRepository<SysRoleMenu, Long> {

    //根据角色id获取菜单id

    @Query("select menu_id from sys_role_menu where role_id in (:roleIds)")
    Flux<Long> findMenuIdByRoleId(Collection<Long> roleIds);
    //根据单个角色id获取菜单id
    @Query("select menu_id from sys_role_menu where role_id = :roleId")
    Flux<Long> findMenuIdByRoleId(Long roleId);

    //根据角色id删除角色菜单关系
    @Query("delete from sys_role_menu where role_id = :roleId")
    Mono<Void> deleteAllByRoleId(Long roleId);

    @Query("DELETE FROM sys_role_menu WHERE role_id = :roleId AND menu_id IN (:menuIds)")
    Mono<Void> deleteAllByRoleIdAndMenuIds(Long roleId, List<Long> menuIds);
    @Query("select * from sys_role_menu where role_id = :roleId")
    Flux<SysRoleMenu> findByRoleId(Long roleId);
}
