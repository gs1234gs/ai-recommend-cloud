package com.guanshiyun.service.userrole.impl;

import com.guanshiyun.controller.userrole.vo.SysUserRoleVO;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<ResultT<SysUserRole>> addUserRole(Long userId, List<Long> roleIds) {
        // 1. 简单校验
        if (roleIds == null || roleIds.isEmpty()) {
            // 如果传空列表，通常意味着清空所有角色
            return sysUserRoleRepository.deleteAllByUserId(userId)
                    .thenReturn(ResultT.<SysUserRole>builder().code(200).msg("清空成功").build());
        }

        return sysUserRoleRepository.findSysUserRoleByUserId(userId)
                .collectList()
                .flatMap(existingList -> {
                    // 提取数据库中已有的角色ID集合
                    Set<Long> existingIds = existingList.stream()
                            .map(SysUserRole::getRoleId)
                            .collect(Collectors.toSet());

                    // 将传入的列表转为 Set，方便去重和快速查找
                    Set<Long> incomingIds = new HashSet<>(roleIds);

                    // --- 核心逻辑：计算差集 ---

                    // 1. 需要删除的：数据库里有，但传入列表里没有 (existing - incoming)
                    List<Long> toDeleteIds = existingIds.stream()
                            .filter(id -> !incomingIds.contains(id))
                            .toList();

                    // 2. 需要新增的：传入列表里有，但数据库里没有 (incoming - existing)
                    List<Long> toAddIds = incomingIds.stream()
                            .filter(id -> !existingIds.contains(id))
                            .toList();

                    // --- 执行操作 ---

                    // 执行删除 (如果没有要删的，返回空Mono)
                    Mono<Void> deleteMono = toDeleteIds.isEmpty()
                            ? Mono.empty()
                            : sysUserRoleRepository.deleteByUserIdAndRoleIdIn(userId, toDeleteIds);

                    // 构建新增对象并执行保存 (如果没有要加的，返回空Flux)
                    Mono<List<SysUserRole>> saveMono = toAddIds.isEmpty()
                            ? Mono.just(Collections.emptyList())
                            : sysUserRoleRepository.saveAll(toAddIds.stream()
                            .map(id -> SysUserRole.builder().userId(userId).roleId(id).build())
                            .toList())
                            .collectList();

                    // 并行执行删除和新增
                    return Mono.when(deleteMono, saveMono)
                            .then(Mono.fromCallable(() -> ResultT.<SysUserRole>builder()
                                    .code(200)
                                    .msg("更新成功")
                                    .build()));
                })
                // 处理用户原本没有任何角色的情况（直接走新增逻辑）
                .switchIfEmpty(Mono.defer(() -> {
                    List<SysUserRole> toAdd = roleIds.stream().distinct()
                            .map(id -> SysUserRole.builder().userId(userId).roleId(id).build())
                            .toList();
                    return sysUserRoleRepository.saveAll(toAdd)
                            .collectList()
                            .thenReturn(ResultT.<SysUserRole>builder().code(200).msg("角色添加成功").build());
                }))
                .onErrorResume(e -> {
                    log.error("更新用户角色失败", e);
                    return Mono.error(new RuntimeException("更新用户角色失败", e));
                });
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
