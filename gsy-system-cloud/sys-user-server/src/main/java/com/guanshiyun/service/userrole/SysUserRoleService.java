package com.guanshiyun.service.userrole;

import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SysUserRoleService {
    //添加用户角色关系
    Mono<ResultT<SysUserRole>> addUserRole(BigInteger userId, BigInteger roleId);
    //根据用户id查询角色关系
    Flux<BigInteger> findRoleIdsByUserId(BigInteger userId);

    Mono<Long> deleteUserRoleByRoleId(List<BigInteger> roleId, BigInteger userId);

    Mono<SysUserRole> addUserRole(SysRelationRequest sysRelationRequest);
}
