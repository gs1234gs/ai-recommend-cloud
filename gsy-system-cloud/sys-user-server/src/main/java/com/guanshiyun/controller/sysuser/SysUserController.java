package com.guanshiyun.controller.sysuser;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysuser.SysUserService;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequestMapping("/sysUser")
@RestController
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    //删除用户
    @DeleteMapping("/delete/{id}")
    public Mono<ResultT<Long>> deleteUserById(@PathVariable BigInteger id) {
        return sysUserService.deleteUserById(id)
                .map(deleteCount -> {
                    if (deleteCount.equals(ConstNumber.INTEGER_ZERO))
                        return ResultT.<Long>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("删除用户失败")
                                .data(deleteCount)
                                .build();
                    return ResultT.<Long>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除用户成功")
                            .data(deleteCount)
                            .build();
                })
                .switchIfEmpty(
                        Mono.just(ResultT.<Long>builder()
                                .code(HttpCodeConst.NOT_FOUND)
                                .msg("删除用户失败")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("删除用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("删除用户失败")
                            .data(null)
                            .build());
                });
    }

    //批量删除用户
    @DeleteMapping("/delete")
    public Mono<ResultT<Long>> deleteUserByIds(@RequestBody Collection<BigInteger> ids) {
        return sysUserService.deleteUserByIds(ids)
                .map(deleteCount -> {
                            if (deleteCount.equals(ConstNumber.INTEGER_ZERO))
                                return ResultT.<Long>builder()
                                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                        .msg("批量删除用户失败")
                                        .data(deleteCount)
                                        .build();
                            return ResultT.<Long>builder()
                                    .code(HttpCodeConst.OK)
                                    .msg("批量删除用户成功")
                                    .data(deleteCount)
                                    .build();
                        }

                )
                .switchIfEmpty(
                        Mono.just(ResultT.<Long>builder()
                                .code(HttpCodeConst.NOT_FOUND)
                                .msg("批量删除用户失败")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("批量删除用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("批量删除用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //获取单个用户信息
    @GetMapping("user/{id}")
    public Mono<ResultT<SysUser>> findById(@PathVariable BigInteger id) {
        return sysUserService.findById(id)
                .map(sysUser ->
                        ResultT.<SysUser>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取用户成功")
                                .data(sysUser)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(ResultT.<SysUser>builder()
                                .code(HttpCodeConst.NOT_FOUND)
                                .msg("用户不存在")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户失败", throwable);
                    return Mono.just(ResultT.<SysUser>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //修改用户
    @PutMapping("/update")
    public Mono<ResultT<SysUser>> updateUserById(@RequestBody SysUser sysUser) {
        return sysUserService.updateUserById(sysUser)
                .map(updateCount ->
                        ResultT.<SysUser>builder()
                                .code(HttpCodeConst.OK)
                                .msg("修改用户成功")
                                .data(sysUser)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<SysUser>builder()
                                        .code(HttpCodeConst.NOT_FOUND)
                                        .msg("用户不存在")
                                        .data(null)
                                        .build()
                        )
                ).onErrorResume(throwable -> {
                    log.error("修改用户失败", throwable);
                    return Mono.just(ResultT.<SysUser>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("修改用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //添加用户
    @PostMapping("/add")
    public Mono<ResultT<SysUser>> addUser(@RequestBody SysUser sysUser) {
        return sysUserService.save(sysUser)
                .flatMap(sysUserSave ->
                        Mono.just(
                                ResultT.<SysUser>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("添加用户成功")
                                        .data(sysUserSave)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<SysUser>builder()
                                        .code(HttpCodeConst.NOT_FOUND)
                                        .msg("添加用户失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("添加用户失败", throwable);
                    return Mono.just(ResultT.<SysUser>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("服务器错误")
                            .data(null)
                            .build());
                })
                ;
    }

    //获取用户列表
    @PostMapping("/list")
    public Mono<ResultT<PageResultT<List<SysUser>>>> findPage(
            @RequestBody(required = false) RequestPage<SysUser> requestPage) {
        return sysUserService.findPage(requestPage)
                .map(pageResult ->
                        ResultT.<PageResultT<List<SysUser>>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取用户列表成功")
                                .data(pageResult)
                                .build()
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户列表失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<SysUser>>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取用户列表失败")
                            .data(null)
                            .build());
                });
    }

}
