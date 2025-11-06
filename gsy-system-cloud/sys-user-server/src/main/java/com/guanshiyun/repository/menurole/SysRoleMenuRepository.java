package com.guanshiyun.repository.menurole;

import com.guanshiyun.relationpojo.SysRoleMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.Collection;

public interface SysRoleMenuRepository extends ReactiveCrudRepository<SysRoleMenu, BigInteger> {

    //根据角色id获取菜单id

    @Query("select menu_id from sys_role_menu where role_id in (:roleIds)")
    Flux<BigInteger> findMenuIdByRoleId(Collection<BigInteger> roleIds);
}
