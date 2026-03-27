package com.guanshiyun.service.userrole.impl;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.userrole.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<ResultT<SysUserRole>> addUserRole(Long userId, Long roleId) {
        SysUserRole sysUserRole = SysUserRole.builder()
                .id(null)
                .userId(userId)
                .roleId(roleId)
                .build();
        return sysUserRoleRepository.findExistsUserRole(userId, roleId)
                .flatMap(existUserRole -> {
                            if (!existUserRole.equals(ConstNumber.LONG_ZERO)) {
                                log.info("用户已经用有次角色");
                               return Mono.error(new Exception("用户已经用有次角色"));
                            }
                            return sysUserRoleRepository.save(sysUserRole)
                                    .map(userRole -> {
                                        log.info("添加用户角色关系成功: {}", userRole);
                                               return ResultT.<SysUserRole>builder()
                                                        .code(HttpStatus.OK.value())
                                                        .msg("添加成功")
                                                        .data(userRole)
                                                        .build();
                                            }
                                    );
                        }

                )
                .onErrorResume(throwable -> {
                    log.error("添加用户角色失败", throwable);
                           return Mono.error(new Exception("添加用户角色失败",throwable));
                        }
                );
    }

    @Override
    public Flux<Long> findRoleIdsByUserId(Long userId) {
        return sysUserRoleRepository.findRoleIdByUserId(userId);
    }

    @Override
    public Mono<Long> deleteUserRoleByRoleId(List<Long> roleIds, Long userId) {
        return databaseClient.sql(
                "delete from sys_user_role where role_id in (:roleId) and user_id = :userId"
                )
                .bind(SysUserRole.Fields.roleId, roleIds)
                .bind(SysUserRole.Fields.userId, userId)
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
