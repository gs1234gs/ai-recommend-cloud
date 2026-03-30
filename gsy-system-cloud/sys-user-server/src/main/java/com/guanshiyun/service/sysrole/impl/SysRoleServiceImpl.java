package com.guanshiyun.service.sysrole.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.db.constsql.SqlConst;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.db.tablename.MyStringUtils;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.sysrole.vo.SysRoleSaveVO;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.repository.menurole.SysRoleMenuRepository;
import com.guanshiyun.repository.sysrole.SysRoleRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import com.guanshiyun.service.sysrole.SysRoleService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleRepository sysRoleRepository;
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final SysRoleMenuRepository sysRoleMenuRepository;

    //添加或者更新角色
    @Override
    public Mono<Long> save(SysRoleSaveVO sysRoleSaveVO) {
        SysRole sysRole = BeanUtil.toBean(sysRoleSaveVO, SysRole.class);
        LocalDateTime now = LocalDateTime.now();

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new Exception("用户ID不存在"));
            }
            Long userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);

            // 新增角色
            if (sysRole.getId() == null) {
                sysRole.setCreateTime(now);
                sysRole.setUpdater(userId);
                return sysRoleRepository.save(sysRole)
                        .flatMap(savedRole -> {
                            List<SysRoleMenu> menusToInsert = sysRoleSaveVO.getMenuIdList().stream()
                                    .map(menuId -> SysRoleMenu.builder()
                                            .roleId(savedRole.getId())
                                            .menuId(menuId)
                                            .build())
                                    .toList();
                            return sysRoleMenuRepository.saveAll(menusToInsert)
                                    .collectList()
                                    .then(Mono.just(savedRole.getId()));
                        }).transform(transactionalOperator::transactional);
            }

            // 更新角色信息
            sysRole.setUpdater(userId);
            sysRole.setUpdateTime(now);

            return r2dbcUpdateHelper.updateIgnoreNull(
                            EntityTableNameUtils.getName(SysRole.class),
                            sysRole,
                            SysRole.Fields.id)
                    .flatMap(id -> sysRoleMenuRepository.findByRoleId(sysRole.getId()).collectList()
                            .flatMap(existingMenus -> {
                                List<Long> existingMenuIds = existingMenus.stream()
                                        .map(SysRoleMenu::getMenuId)
                                        .toList();
                                List<Long> newMenuIds = sysRoleSaveVO.getMenuIdList();

                                // 差集计算
                                List<Long> toDelete = existingMenuIds.stream()
                                        .filter(menuId -> !newMenuIds.contains(menuId))
                                        .toList();
                                List<SysRoleMenu> toInsert = newMenuIds.stream()
                                        .filter(menuId -> !existingMenuIds.contains(menuId))
                                        .map(menuId -> SysRoleMenu.builder()
                                                .roleId(sysRole.getId())
                                                .menuId(menuId)
                                                .build())
                                        .toList();

                                Mono<Void> insertMono = toInsert.isEmpty() ? Mono.empty() :
                                        sysRoleMenuRepository.saveAll(toInsert).then();
                                Mono<Void> deleteMono = toDelete.isEmpty() ? Mono.empty() :
                                        sysRoleMenuRepository.deleteAllByRoleIdAndMenuIds(sysRole.getId(), toDelete);

                                return Mono.when(insertMono, deleteMono).then(Mono.just(id));
                            })
                    ).transform(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<PageResultT<List<SysRoleVO>>> findPage(RequestPage<SysRoleVO> requestPage) {
        requestPage = PageUtils.pageValidation(requestPage,SysRoleVO.class);
        // 前端没传 pageSize 时默认10条
        Long pageNum = requestPage.getPageNum();
        int pageSize = requestPage.getPageSize();
        // 条件
        Criteria criteria = Criteria.empty();
        String name = requestPage.getCondition().getName();
        //不为空，模糊查询
        if (StrUtil.isNotBlank(name)) {
            criteria = criteria.and(SysRole.Fields.name).like(SqlConst.PERCENT + name + SqlConst.PERCENT);
        }
        long offset =
                (pageNum -1)*pageSize;
        // 数据查询：按 createTime 降序，推荐加上 id 作为二级排序
        Query dataQuery = Query.query(criteria)
                .sort(Sort.by(
                        Sort.Order.desc(MyStringUtils.camelToUnderlineSmart(BasePojo.Fields.createTime)),
                        Sort.Order.desc(SysRole.Fields.id) // 防止 createTime 重复导致数据错位
                ))
                .offset(offset)
                .limit(pageSize);
        // 总数查询
        Query countQuery = Query.query(criteria);
        return template.select(countQuery, SysRole.class)
                .count()
                .flatMap(count -> template.select(dataQuery, SysRole.class)
                        .map(role -> BeanUtil.toBean(role, SysRoleVO.class))
                        .collectList()
                        .map(list -> PageResultT.<List<SysRoleVO>>builder()
                                .total(count)
                                .rows(list)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("查询角色列表异常", throwable);
                    return Mono.just(PageResultT.<List<SysRoleVO>>builder().build());
                });
    }

    @Override
    public Mono<Long> deleteRoleById(Long id) {
        return databaseClient.sql("DELETE FROM sys_role WHERE id = :id")
                .bind(SysRole.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->
                        databaseClient.sql("delete from sys_role_menu where role_id = :roleId")
                                .bind(
                                        SysRoleMenu.Fields.roleId
                                , id)
                                .fetch()
                                .rowsUpdated()
                                .flatMap(rowsChildren ->
                                        databaseClient.sql("delete from sys_user_role where role_id = :roleId")
                                                .bind(
                                                        SysRoleMenu.Fields.roleId, id)
                                                .fetch()
                                                .rowsUpdated()
                                                .thenReturn(rowsUpdated)
                                )
                )
                .as(transaction ->
                        transaction.as(transactionalOperator::transactional)
                );
    }

    //根据用户id获取角色
    @Override
    public Flux<SysRole> findAllByUserId(Long userId) {
        return sysUserRoleRepository.findRoleIdByUserId(userId)
                .collectList()
                .flatMapMany(sysRoleRepository::findAllById);
    }

    @Override
    public Mono<SysRoleVO> findById(Long id) {
        return sysRoleRepository.findById(id)
                .map(sysRole -> BeanUtil.toBean(sysRole, SysRoleVO.class));
    }

    @Override
    public Mono<Long> update(SysRoleSaveVO sysRoleSaveVO) {
        return r2dbcUpdateHelper.updateIgnoreNull(
                EntityTableNameUtils.getName(SysRole.class),
                sysRoleSaveVO,
                SysRole.Fields.id
        );
    }
}
