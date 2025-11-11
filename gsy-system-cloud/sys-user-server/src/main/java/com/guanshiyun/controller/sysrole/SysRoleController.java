package com.guanshiyun.controller.sysrole;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.sysrole.vo.SysRoleSaveVO;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysrole.SysRoleService;
//import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sysRole/")
@RequiredArgsConstructor
public class SysRoleController {
    private final SysRoleService sysRoleService;

    //添加角色或者修改角色
//    @Operation(summary = "添加角色或者修改角色")
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody SysRoleSaveVO sysRoleSaveVO) {
        return sysRoleService.save(sysRoleSaveVO)
                .flatMap(id ->
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("成功")
                                        .data(id)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(
                        throwable -> {
                            log.error("添加角色失败", throwable);
                            return Mono.just(
                                    ResultT.<BigInteger>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("服务器错误")
                                            .data(null)
                                            .build()
                            );
                        }
                );
    }

    //删除角色
    @DeleteMapping("deleteById/{id}")
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
    @PutMapping("updateById")
    public Mono<ResultT<BigInteger>> updateRole(@RequestBody SysRoleSaveVO sysRoleVO) {
        return sysRoleService.update(sysRoleVO)
                .flatMap(id ->
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("修改成功")
                                        .data(id)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<BigInteger>builder()
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
                                    ResultT.<BigInteger>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("修改角色失败")
                                            .data(null)
                                            .build()
                            );
                        }
                );

    }

    //查询角色
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<SysRoleVO>>>> roleList(
            @RequestBody(required = false) RequestPage<SysRoleVO> requestPage) {
        return sysRoleService.findPage(requestPage)
                .map(pageResult ->
                        ResultT.<PageResultT<List<SysRoleVO>>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取用户列表成功")
                                .data(pageResult)
                                .build()
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户列表失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<SysRoleVO>>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取用户列表失败")
                            .data(null)
                            .build());
                });
    }

    //根据用户id获取角色
    @GetMapping("findRoleListByUserId/{userId}")
    public Mono<ResultT<List<SysRoleVO>>> roleList(@PathVariable BigInteger userId) {
        return sysRoleService.findAllByUserId(userId)
                .map(role -> BeanUtil.toBean(role, SysRoleVO.class))
                .collectList()
                .map(roleList ->
                        ResultT.<List<SysRoleVO>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取角色列表成功")
                                .data(roleList)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysRoleVO>>builder()
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
                                    ResultT.<List<SysRoleVO>>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("获取角色列表失败")
                                            .data(null)
                                            .build()
                            );
                        }
                );
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<SysRoleVO>> findById(@PathVariable BigInteger id) {
        return sysRoleService.findById(id)
                .map(role ->
                        ResultT.<SysRoleVO>builder()
                                .code(HttpCodeConst.OK)
                                .msg("获取角色成功")
                                .data(role)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<SysRoleVO>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("获取角色失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(
                        throwable -> {
                            log.error("获取角色失败", throwable);
                            return Mono.just(
                                    ResultT.<SysRoleVO>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("获取角色失败")
                                            .data(null)
                                            .build()
                            );
                        }
                );
    }


}
