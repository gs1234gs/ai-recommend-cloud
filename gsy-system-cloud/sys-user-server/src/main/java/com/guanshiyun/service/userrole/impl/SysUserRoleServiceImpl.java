package com.guanshiyun.service.userrole.impl;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.SqlConstRepository;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.userrole.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<ResultT<SysUserRole>> addUserRole(BigInteger userId, BigInteger roleId) {
        SysUserRole sysUserRole = SysUserRole.builder()
                .id(null)
                .userId(userId)
                .roleId(roleId)
                .build();
        return sysUserRoleRepository.existsByUserIdAndRoleId(userId, roleId)
                .flatMap(existUserRole -> {
                            if (existUserRole.equals(ConstNumber.LONG_ZERO))
                                return Mono.just(ResultT.<SysUserRole>builder()
                                        .code(HttpCodeConst.CONFLICT)
                                        .msg("已拥有此角色")
                                        .data(null)
                                        .build());
                            return sysUserRoleRepository.save(sysUserRole)
                                    .map(userRole -> ResultT.<SysUserRole>builder()
                                            .code(HttpCodeConst.OK)
                                            .msg("添加成功")
                                            .data(userRole)
                                            .build());
                        }

                )
                .onErrorResume(throwable -> Mono.just(ResultT
                        .<SysUserRole>builder()
                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                        .msg("添加失败")
                        .data(null)
                        .build()
                ));
    }

    @Override
    public Flux<BigInteger> findRoleIdsByUserId(BigInteger userId) {
        return sysUserRoleRepository.findRoleIdByUserId(userId);
    }

    @Override
    public Mono<Long> deleteUserRoleByRoleId(List<BigInteger> roleIds, BigInteger userId) {
        return databaseClient.sql(
                "delete from sys_user_role where role_id in (:roleIds) and user_id = :userId"
                )
                .bind(SqlConstRepository.ROLE_IDS, roleIds)
                .bind(SqlConstRepository.USER_ID, userId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<SysUserRole> addUserRole(SysRelationRequest sysRelationRequest) {
        SysUserRole sysUserRole = SysUserRole.builder()
                .id(null)
                .userId(sysRelationRequest.getEntityId())
                .roleId(sysRelationRequest.getRoleId())
                .build();
        return sysUserRoleRepository.save(sysUserRole);
    }
}
