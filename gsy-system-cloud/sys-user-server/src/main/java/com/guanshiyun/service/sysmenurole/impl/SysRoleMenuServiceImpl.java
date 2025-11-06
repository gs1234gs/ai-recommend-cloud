package com.guanshiyun.service.sysmenurole.impl;

import com.guanshiyun.consts.SqlConstRepository;
import com.guanshiyun.relation.SysRelationRequest;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.repository.menurole.SysRoleMenuRepository;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final DatabaseClient databaseClient;
    @Override
    public Flux<BigInteger> findMenuIdsByRoleId(Collection<BigInteger> roleIds) {
        return sysRoleMenuRepository.findMenuIdByRoleId(roleIds);
    }

    @Override
    public Mono<SysRoleMenu> addRoleMenu(SysRelationRequest sysRelationRequest) {
        SysRoleMenu sysRoleMenu = SysRoleMenu.builder()
                .id(null)
                .roleId(sysRelationRequest.getRoleId())
                .menuId(sysRelationRequest.getEntityId())
                .build();
        return sysRoleMenuRepository.save(sysRoleMenu);
    }

    @Override
    public Mono<Long> deleteRoleMenu(BigInteger roleId, List<BigInteger> menuIds) {
        return databaseClient.sql("delete from sys_role_menu where role_id = :roleId and menu_id in (:menuIds)")
                .bind(SqlConstRepository.ROLE_ID, roleId)
                .bind(SqlConstRepository.MENU_IDS, menuIds)
                .fetch()
                .rowsUpdated();
    }
}
