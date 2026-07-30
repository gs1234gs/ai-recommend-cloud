package com.guanshiyun.service.sysrole.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactiveQuery;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.sysrole.vo.SysRoleSaveVO;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.repository.menurole.SysRoleMenuRepository;
import com.guanshiyun.repository.sysrole.SysRoleRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import com.guanshiyun.service.sysrole.SysRoleService;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private final MyLong myLong;
    private final ReactiveQuery reactiveQuery;

    //添加或者更新角色
    @Override
    public Mono<Long> save(SysRoleSaveVO sysRoleSaveVO) {
        SysRole sysRole = BeanUtil.toBean(sysRoleSaveVO, SysRole.class);
        LocalDateTime now = LocalDateTime.now();

        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Exception("用户ID不存在"));
            }
            Long userId = myLong.findUserId(ctx);

            // 新增角色
            if (Objects.isNull(sysRole.getId())) {
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
                            SysRole.class,
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
        RequestPage<SysRole> pageRequest = BeanConvertUtil.toBean(requestPage, SysRole.class);
        SysRole condition = pageRequest.getCondition();
      return Mono.deferContextual(ctx->{
          Boolean hasKey = myLong.hasKey(ctx);
          if(!hasKey){
              return Mono.error(new Exception("用户不存在"));
          }
          Long tenantId = myLong.findTenantId(ctx);
          return reactiveQuery.createQuery(SysRole.class, pageRequest)
                   .like(SysRole.Fields.name, condition.getName())
                   .orderByDesc(BasePojo.Fields.createTime,SysRole.Fields.id)
                   .page()
                   .map(page -> BeanConvertUtil.toBean(page, SysRoleVO.class))
                   .onErrorResume(throwable -> {
                       log.error("查询角色列表异常", throwable);
                       return Mono.just(PageResultT.<List<SysRoleVO>>builder().build());
                   });
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
                                .bind(SysRoleMenu.Fields.roleId, id)
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
                SysRole.class,
                BeanConvertUtil.toBean(sysRoleSaveVO, SysRole.class),
                SysRole.Fields.id
        );
    }
}
