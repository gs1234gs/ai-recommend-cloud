package com.guanshiyun.controller.sysrole;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rolepojo.SysRole;
import com.guanshiyun.service.sysrole.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sysRole")
@RequiredArgsConstructor
public class SysRoleController {
    private final SysRoleService sysRoleService;

    //添加角色
    @PostMapping("/add")
    public Mono<ResultT<Object>> addRole(@RequestBody SysRole sysRole) {
        return sysRoleService.save(sysRole)
                .collectList()
                .flatMap(roleList ->
                        Mono.just(
                                ResultT.builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("添加成功")
                                        .data(roleList)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("添加失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(
                        throwable -> {
                            log.error("添加角色失败", throwable);
                            return Mono.just(
                                    ResultT.builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("服务器错误")
                                            .data(null)
                                            .build()
                            );
                        }
                );
    }

    //删除角色
    @DeleteMapping("/delete/{id}")
    public Mono<ResultT<Long>> deleteRole(@PathVariable BigInteger id) {
        return sysRoleService.deleteRoleById(id)
                .map(result ->
                        ResultT.<Long>builder()
                                .code(HttpCodeConst.OK)
                                .msg("删除成功")
                                .data(result)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<Long>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("删除失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("删除角色失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("删除角色失败,系统错误")
                            .data(null)
                            .build()
                    );
                });
    }

    //修改角色
    @PutMapping("/update")
    public Mono<ResultT<List<SysRole>>> updateRole(@RequestBody SysRole sysRole) {
        return sysRoleService.save(sysRole)
                .collectList()
                .flatMap(roleList ->
                        Mono.just(
                                ResultT.<List<SysRole>>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("修改成功")
                                        .data(roleList)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysRole>>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("修改失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(
                        throwable -> {
                            log.error("修改角色失败", throwable);
                            return Mono.just(
                                    ResultT.<List<SysRole>>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("修改角色失败")
                                            .data(null)
                                            .build()
                            );
                        }
                );

    }

    //查询角色
    @PostMapping("roleList")
    public Mono<ResultT<PageResultT<List<SysRole>>>> roleList(
            @RequestBody(required = false) RequestPage<SysRole> requestPage) {
        return sysRoleService.findPage(requestPage)
                .map(pageResult ->
                        ResultT.<PageResultT<List<SysRole>>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取用户列表成功")
                                .data(pageResult)
                                .build()
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户列表失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<SysRole>>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取用户列表失败")
                            .data(null)
                            .build());
                });
    }

    //根据用户id获取角色
    @GetMapping("roleList/{userId}")
    public Mono<ResultT<List<SysRole>>> roleList(@PathVariable BigInteger userId) {
        return sysRoleService.findAllByUserId(userId)
                .collectList()
                .map(roleList ->
                        ResultT.<List<SysRole>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取角色列表成功")
                                .data(roleList)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysRole>>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("获取角色列表失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(
                        throwable -> {
                            log.error("获取角色列表失败", throwable);
                            return Mono.just(
                                    ResultT.<List<SysRole>>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("获取角色列表失败")
                                            .data(null)
                                            .build()
                            );
                        }
                );
    }

}
