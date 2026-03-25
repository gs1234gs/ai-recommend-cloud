package com.guanshiyun.service.sysmenu;

import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Collection;

public interface SysMenuService {
    //根据菜单id列表
    Flux<SysMenu> findByIds(Collection<Long> menuIds);
    //根据用户id获取菜单
    Flux<SysMenu> findMenuByUserId();

    Flux<SysMenu> findAllByParentId(Long id);

    Mono<Long> deleteById(Long id);

    Mono<Long> save(SysMenu sysMenu);

   Mono<Long> updateById(SysMenu sysMenu);

    Flux<SysMenu> findAll();

    Flux<SysMenu> findMenuByRoleId(Long roleId);

    Mono<SysMenuResponse> findById(Long id);
}
