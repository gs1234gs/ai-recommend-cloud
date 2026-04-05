package com.guanshiyun.service.userrole.impl;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.userrole.vo.SysUserRoleVO;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Mono<SysUserRole> addUserRole(SysUserRoleVO sysUserRoleVO) {
        Long userId = sysUserRoleVO.getUserId();
        List<Long> requestRoleIds = sysUserRoleVO.getRoleId();
        // 1. 查询数据库中当前已存在的角色关系
        return sysUserRoleRepository.findSysUserRoleByUserId(userId) // 返回 Flux<SysUserRole>
                .map(SysUserRole::getRoleId)              // 提取 roleId
                .collectList()                            // 收集为 List<Long>
                .flatMap(dbExistingRoleIds -> {

                    // 转换为 Set 以提高 contains 查询效率 (O(1))
                    Set<Long> dbRoleIdSet = new HashSet<>(dbExistingRoleIds);
                    Set<Long> requestRoleIdSet = new HashSet<>(requestRoleIds);

                    // --- 核心算法：计算差集 ---

                    // 2. 找出需要【新增】的：在请求中，但不在数据库中
                    List<Long> toInsertIds = requestRoleIds.stream()
                            .filter(id -> !dbRoleIdSet.contains(id))
                            .toList();

                    // 3. 找出需要【删除】的：在数据库中，但不在请求中
                    List<Long> toDeleteIds = dbExistingRoleIds.stream()
                            .filter(id -> !requestRoleIdSet.contains(id))
                            .collect(Collectors.toList());

                    // --- 执行数据库操作 ---

                    List<Mono<Void>> operations = new ArrayList<>();

                    // 构建插入任务
                    if (!toInsertIds.isEmpty()) {
                        List<SysUserRole> toInsertEntities = toInsertIds.stream()
                                .map(id -> SysUserRole.builder()
                                        .userId(userId)
                                        .roleId(id)
                                        .build())
                                .collect(Collectors.toList());
                        // 假设你有 saveAll 方法
                        operations.add(sysUserRoleRepository.saveAll(toInsertEntities).then());
                    }

                    // 构建删除任务
                    if (!toDeleteIds.isEmpty()) {
                        // 假设你有 deleteByUserIdAndRoleIdIn 方法
                        operations.add(sysUserRoleRepository.deleteByUserIdAndRoleIdIn(userId, toDeleteIds));
                    }

                    // 4. 并发或顺序执行所有变更操作
                    // 使用 concat 保证顺序执行，或者 merge 并发执行（取决于数据库事务隔离要求）
                    if (operations.isEmpty()) {
                        // 如果没有变化，直接返回空或现有状态
                        return Mono.empty();
                    }

                    return Flux.fromIterable(operations)
                            .concatMap(mono -> mono) // 串行执行，确保数据一致性
                            .then(); // 等待所有操作完成
                })
                .then(Mono.fromCallable(() -> {
                    // 5. 返回结果（这里简单返回一个构建好的对象，或者你可以重新查询最新状态）
                    return SysUserRole.builder()
                            .userId(userId)
                            .roleId(requestRoleIds.isEmpty() ? null : requestRoleIds.get(0)) // 仅示例
                            .build();
                }));
    }
}
