package com.guanshiyun.service.sysmenurole;

import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysRoleMenu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Collection;
import java.util.List;

public interface SysRoleMenuService {

    //根据角色id查询菜单id
    Flux<Long> findMenuIdsByRoleId(Collection<Long> roleIds);

    Mono<SysRoleMenu> addRoleMenu(SysRelationRequest sysRelationRequest);

    Mono<Long> deleteRoleMenu(Long roleId, List<Long> menuIds);
}
