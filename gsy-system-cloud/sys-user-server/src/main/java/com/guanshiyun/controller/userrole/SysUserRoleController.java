package com.guanshiyun.controller.userrole;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.userrole.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/userRole")
@RequiredArgsConstructor
public class SysUserRoleController {
    private final SysUserRoleService sysUserRoleService;
    //添加用户角色关系
    @PostMapping("/add")
    public Mono<ResultT<SysUserRole>> addUserRole(
            @RequestBody SysRelationRequest sysRelationRequest
            ){
        return sysUserRoleService.addUserRole(sysRelationRequest)
                .map(addUserRole -> ResultT.<SysUserRole>builder()
                        .code(HttpCodeConst.OK)
                        .msg("添加用户角色关系成功")
                        .data(addUserRole)
                        .build())
                .switchIfEmpty(
                        Mono.just(ResultT.<SysUserRole>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("添加用户角色关系失败")
                                .data(null)
                                .build())
                )
                .onErrorResume(throwable -> Mono.just(
                        ResultT.<SysUserRole>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("添加用户角色关系失败")
                                .data(null)
                                .build()
                ));
    }
    //删除用户角色关系
    @DeleteMapping("/delete")
    public Mono<ResultT<Long>> deleteUserRoleByRoleId(
            @RequestParam (required = false) BigInteger userId,
            @RequestParam (required = false) List<BigInteger> roleId){
        if(Objects.isNull(roleId) || Objects.isNull(userId))
            return Mono.just(ResultT.<Long>builder()
                    .code(HttpCodeConst.BAD_REQUEST)
                    .msg("参数错误")
                    .data(ConstNumber.LONG_ZERO)
                    .build());
        return sysUserRoleService.deleteUserRoleByRoleId(roleId, userId)
                .map(deleteCount -> {
                    if(deleteCount.equals(ConstNumber.LONG_ZERO))
                        return ResultT.<Long>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("删除用户角色关系失败")
                                .data(deleteCount)
                                .build();
                    return ResultT.<Long>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除用户角色关系成功")
                            .data(deleteCount)
                            .build();
                        }
                        )
                .onErrorResume(
                        throwable -> {
                            log.error("删除用户角色关系失败", throwable);
                            return Mono.just(ResultT.<Long>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除用户角色关系失败")
                                    .data(ConstNumber.LONG_ZERO)
                                    .build());
                        }
                );
    }
    //修改用户角色关系
    //查询用户角色关系
    @GetMapping("/roleList/{userId}")
    public Mono<ResultT<List<BigInteger>>> findRoleIdsByUserId(@PathVariable BigInteger userId){
        return sysUserRoleService.findRoleIdsByUserId(userId)
                .collectList()
                .flatMap(roleIds ->{
                    if(roleIds.isEmpty())
                        return Mono.just(ResultT.<List<BigInteger>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("查询用户角色关系成功")
                                .data(roleIds)
                                .build());
                    return Mono.just(ResultT.<List<BigInteger>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询用户角色关系成功")
                            .data(roleIds)
                            .build());
                        }
                        );
    }

}
