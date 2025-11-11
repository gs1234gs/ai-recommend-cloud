package com.guanshiyun.repository.menurole;

import com.guanshiyun.relationpojo.SysRoleMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;

public interface SysRoleMenuRepository extends ReactiveCrudRepository<SysRoleMenu, BigInteger> {

    //根据角色id获取菜单id

    @Query("select menu_id from sys_role_menu where role_id in (:roleIds)")
    Flux<BigInteger> findMenuIdByRoleId(Collection<BigInteger> roleIds);
    //根据单个角色id获取菜单id
    @Query("select menu_id from sys_role_menu where role_id = :roleId")
    Flux<BigInteger> findMenuIdByRoleId(BigInteger roleId);

    //根据角色id删除角色菜单关系
    @Query("delete from sys_role_menu where role_id = :roleId")
    Mono<Void> deleteAllByRoleId(BigInteger roleId);
}
