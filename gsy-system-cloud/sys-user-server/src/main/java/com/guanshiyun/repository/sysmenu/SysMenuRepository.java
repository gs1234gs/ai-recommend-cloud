package com.guanshiyun.repository.sysmenu;

import com.guanshiyun.menupojo.SysMenu;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface SysMenuRepository extends ReactiveCrudRepository<SysMenu, BigInteger> {
    // 根据父级id查询菜单
//    @Query("SELECT * FROM sys_menu WHERE parent_id = :parentId")
    Flux<SysMenu> findAllByParentId(BigInteger parentId);
}
