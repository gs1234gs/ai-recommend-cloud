package com.guanshiyun.service.sysmenu.impl;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.menurole.SysRoleMenuRepository;
import com.guanshiyun.repository.sysmenu.SysMenuRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {
    private final SysMenuRepository sysMenuRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;
    private final MyLong myLong;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;

    @Override
    public Flux<SysMenu> findByIds(Collection<Long> menuIds) {

        return sysMenuRepository.findAllById(menuIds);
    }

    @Override
    public Flux<SysMenu> findMenuByUserId() {
        return Flux.deferContextual(ctx -> {
                    if (!myLong.hasKey(ctx)) {
                        return Flux.error(new RuntimeException("用户未登录"));
                    }
                    Long userId = myLong.findUserId(ctx);
                    return sysUserRoleRepository.findRoleIdByUserId(userId)
                            .collectList()
                            .flatMap(roleIds ->
                                    sysRoleMenuRepository.findMenuIdByRoleId(roleIds)
                                            .distinct()
                                            .collectList()
                            );
                })
                .flatMap(sysMenuRepository::findAllById);
//        return sysUserRoleRepository.findRoleIdByUserId(userId)
//                .collectList()
//                .flatMap(roleIds ->
//                        sysRoleMenuRepository.findMenuIdByRoleId(roleIds)
//                                .distinct()
//                                .collectList()
//                )
//                .flatMapMany(menuIds -> sysMenuRepository.findAllById(menuIds));
    }

    @Override
    public Flux<SysMenu> findAllByParentId(Long id) {
        if (Objects.isNull(id))
            id = ConstNumber.LONG_ZERO;
        return sysMenuRepository.findAllByParentId(id);
    }

    //    @Override
//    public Mono<Long> deleteById(Long id) {
//        if (Objects.isNull(id))
//            return Mono.just(ConstNumber.LONG_ZERO);
//        //避免误操作，只能删除id大于100000的菜单
//        if (id.compareTo(Long.valueOf(131)) < 0)
//            return Mono.just(ConstNumber.LONG_ZERO);
//        return databaseClient.sql("delete from sys_menu where id = :id")
//                .bind(SysMenu.Fields.id, id)
//                .fetch()
//                .rowsUpdated()
//                .flatMap(rowsUpdated ->
//                        {
//                            //删除子集，孙子级
//                            //查询子集
//                            sysMenuRepository.findAllByParentId( id);
//                           return databaseClient.sql("delete from sys_role_menu where menu_id = :menuId")
//                                    .bind(SysRoleMenu.Fields.menuId, id)
//                                    .fetch()
//                                    .rowsUpdated()
//                                    .thenReturn(rowsUpdated);
//                        }
//                )
//                .as(transaction -> transaction.as(transactionalOperator::transactional));
//    }
    @Override
    public Mono<Long> deleteById(Long id) {
        if (id == null || id.compareTo(Long.valueOf(131)) <= 0) {
            return Mono.just(ConstNumber.LONG_ZERO);
        }

        return collectAllDescendantIds(id)
                .flatMap(allIds -> {
                    if (allIds.isEmpty()) {
                        return Mono.just(0L);
                    }

                    String placeholders = allIds.stream()
                            .map(i -> "?")
                            .collect(Collectors.joining(", "));

                    // 删除角色菜单关联
                    Mono<Long> deleteRoleMenu = bindList(
                            databaseClient.sql("DELETE FROM sys_role_menu WHERE menu_id IN (" + placeholders + ")"),
                            allIds
                    )
                            .fetch()
                            .rowsUpdated();

                    // 删除菜单
                    Mono<Long> deleteMenu = bindList(
                            databaseClient.sql("DELETE FROM sys_menu WHERE id IN (" + placeholders + ")"),
                            allIds
                    )
                            .fetch()
                            .rowsUpdated();

                    return deleteRoleMenu.then(deleteMenu)
                            .as(transactionalOperator::transactional);
                });
    }

    // 辅助方法：绑定列表到 SQL 参数（按位置）
    private DatabaseClient.GenericExecuteSpec bindList(DatabaseClient.GenericExecuteSpec spec, List<?> values) {
        for (int i = 0; i < values.size(); i++) {
            spec = spec.bind(i, values.get(i));
        }
        return spec;
    }

    /**
     * 使用响应式 BFS（广度优先）收集所有后代菜单 ID（包括自己）
     */
    private Mono<List<Long>> collectAllDescendantIds(Long rootId) {
        return Mono.just(Collections.singletonList(rootId)) // 初始层：[rootId]
                .expand(currentBatch -> {
                    if (currentBatch.isEmpty()) {
                        return Mono.empty(); // 终止 expand
                    }

                    String inClause = currentBatch.stream()
                            .map(i -> "?")
                            .collect(Collectors.joining(", "));

                    //  使用 bindList 工具方法绑定参数列表
                    return bindList(
                            databaseClient.sql("SELECT id FROM sys_menu WHERE parent_id IN (" + inClause + ")"),
                            currentBatch
                    )
                            .fetch()
                            .all()
                            .map(row -> myLong.myLong(row.get(SysMenu.Fields.id)))
                            .collectList()
                            .filter(nextBatch -> !nextBatch.isEmpty());
                })
                .reduce(new ArrayList<>(), (acc, batch) -> {
                    acc.addAll(batch);
                    return acc;
                });
    }

    @Override
    public Mono<Long> save(SysMenu sysMenu) {

        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.findUserId(ctx);
            if (Objects.isNull(sysMenu.getId())) {
                sysMenu.setCreateTime(LocalDateTime.now());
                sysMenu.setCreator(userId);
                return sysMenuRepository.save(sysMenu)
                        .map(save -> sysMenu.getId());
            }
            sysMenu.setUpdater(userId);
            sysMenu.setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                            SysMenu.class,
                            sysMenu,
                            SysMenu.Fields.id
                    )
                    .map(update -> sysMenu.getId());
        });
    }

    @Override
    public Mono<Long> updateById(SysMenu sysMenu) {
        sysMenu.setUpdateTime(LocalDateTime.now());
        return r2dbcUpdateHelper.updateIgnoreNull(
                SysMenu.class,
                sysMenu,
                SysMenu.Fields.id
        );
    }

    @Override
    public Flux<SysMenu> findAll() {
        return sysMenuRepository.findAll();
    }

    @Override
    public Flux<SysMenu> findMenuByRoleId(Long roleId) {
        return sysRoleMenuRepository.findMenuIdByRoleId(roleId)
                .collectList()
                .flatMapMany(sysMenuRepository::findAllById);
    }

    @Override
    public Mono<SysMenuResponse> findById(Long id) {
        return sysMenuRepository.findById(id)
                .map(sysMenu -> BeanConvertUtil.toBean(sysMenu, SysMenuResponse.class));
    }
}
