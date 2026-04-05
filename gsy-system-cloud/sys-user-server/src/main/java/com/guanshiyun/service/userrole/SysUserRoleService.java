package com.guanshiyun.service.userrole;

import com.guanshiyun.controller.userrole.vo.SysUserRoleVO;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysUserRoleService {
    //添加用户角色关系
    Mono<ResultT<SysUserRole>> addUserRole(Long userId, Long roleId);
    //根据用户id查询角色关系
    Flux<Long> findRoleIdsByUserId(Long userId);

    Mono<Long> deleteUserRoleByRoleId(List<Long> roleId, Long userId);

    Mono<SysUserRole> addUserRole(SysUserRoleVO sysUserRoleVO);
}
