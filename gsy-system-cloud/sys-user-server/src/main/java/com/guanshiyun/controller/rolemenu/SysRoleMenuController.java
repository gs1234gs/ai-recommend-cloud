package com.guanshiyun.controller.rolemenu;

import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/roleMenu/")
@RequiredArgsConstructor
public class SysRoleMenuController {
    private final SysRoleMenuService sysRoleMenuService;

    //添加角色菜单
    @PostMapping("/save")
    public Mono<ResultT<SysRoleMenu>> addRoleMenu(@RequestBody SysRelationRequest sysRelationRequest) {
        return sysRoleMenuService.addRoleMenu(sysRelationRequest)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("添加角色菜单失败")))
                .onErrorResume(throwable -> Mono.just(ResultT.error("添加角色菜单失败" + throwable.getMessage())));
    }
    //删除角色菜单
    @DeleteMapping("/delete")
    public Mono<ResultT<Long>> deleteRoleMenu(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        return sysRoleMenuService.deleteRoleMenu(roleId, menuIds)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("删除角色菜单失败")))
                .onErrorResume(throwable -> Mono.just(ResultT.error("删除角色菜单失败" + throwable.getMessage())));
    }
    //查询角色菜单
    //修改角色菜单
}
