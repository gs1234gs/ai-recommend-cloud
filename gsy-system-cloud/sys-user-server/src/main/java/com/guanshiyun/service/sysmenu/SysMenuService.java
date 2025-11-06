package com.guanshiyun.service.sysmenu;

import com.guanshiyun.menupojo.SysMenu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;

public interface SysMenuService {
    //根据菜单id列表
    Flux<SysMenu> findByIds(Collection<BigInteger> menuIds);
    //根据用户id获取菜单
    Flux<SysMenu> findMenuByUserId();

    Flux<SysMenu> findAllByParentId(BigInteger id);

    Mono<Long> deleteById(BigInteger id);

    Flux<SysMenu> save(SysMenu sysMenu);

    Flux<SysMenu> updateById(SysMenu sysMenu);

    Flux<SysMenu> findAll();

}
