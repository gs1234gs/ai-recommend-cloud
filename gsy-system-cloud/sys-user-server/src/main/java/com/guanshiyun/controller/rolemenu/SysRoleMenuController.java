package com.guanshiyun.controller.rolemenu;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .map(addRoleMenu -> ResultT.<SysRoleMenu>builder()
                        .code(HttpStatus.OK.value())
                        .msg("添加角色菜单成功")
                        .data(addRoleMenu)
                        .build())
                .switchIfEmpty(
                        Mono.just(ResultT.<SysRoleMenu>builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .msg("添加角色菜单失败")
                                .data(null)
                                .build())
                )
                .onErrorResume(throwable -> Mono.just(
                                ResultT.<SysRoleMenu>builder()
                                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .msg("添加角色菜单失败")
                                        .data(null)
                                        .build()

                        )
                );
    }
    //删除角色菜单
    @DeleteMapping("/delete")
    public Mono<ResultT<Long>> deleteRoleMenu(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        return sysRoleMenuService.deleteRoleMenu(roleId, menuIds)
                .map(deleteCount -> ResultT.<Long>builder()
                        .code(HttpStatus.OK.value())
                        .msg("删除角色菜单成功")
                        .data(deleteCount)
                        .build())
                .switchIfEmpty(
                        Mono.just(ResultT.<Long>builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .msg("删除角色菜单失败")
                                .build()
                        )
                )
                .onErrorResume(throwable -> Mono.just(
                                ResultT.<Long>builder()
                                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .msg("删除角色菜单失败")
                                        .data(ConstNumber.LONG_ZERO)
                                        .build()
                        )
                );
    }
    //查询角色菜单
    //修改角色菜单
}
