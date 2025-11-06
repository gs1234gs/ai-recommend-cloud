package com.guanshiyun.service.sysmenu.impl;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.relationpojo.SysRoleMenu;
import com.guanshiyun.repository.menurole.SysRoleMenuRepository;
import com.guanshiyun.repository.sysmenu.SysMenuRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {
    private final SysMenuRepository sysMenuRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;
    private final MyBigInteger myBigInteger;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;

    @Override
    public Flux<SysMenu> findByIds(Collection<BigInteger> menuIds) {

        return sysMenuRepository.findAllById(menuIds);
    }

    @Override
    public Flux<SysMenu> findMenuByUserId() {
        return Flux.deferContextual(ctx -> {
                    if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                        return Flux.error(new RuntimeException("用户未登录"));
                    }
                    BigInteger userId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );
                    return sysUserRoleRepository.findRoleIdByUserId(userId)
                            .collectList()
                            .flatMap(roleIds ->
                                    sysRoleMenuRepository.findMenuIdByRoleId(roleIds)
                                            .distinct()
                                            .collectList()
                            );
                })
                .flatMap(menuIds -> sysMenuRepository.findAllById(menuIds));
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
    public Flux<SysMenu> findAllByParentId(BigInteger id) {
        if (Objects.isNull(id))
            id = BigInteger.ZERO;
        return sysMenuRepository.findAllByParentId(id);
    }

    @Override
    public Mono<Long> deleteById(BigInteger id) {
        if (Objects.isNull(id))
            return Mono.just(ConstNumber.LONG_ZERO);
        //避免误操作，只能删除id大于100000的菜单
        if (id.compareTo(BigInteger.valueOf(100000)) < 0)
            return Mono.just(ConstNumber.LONG_ZERO);
        return databaseClient.sql("delete from sys_menu where id = :id")
                .bind(SysMenu.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->
                        databaseClient.sql("delete from sys_role_menu where menu_id = :menuId")
                                .bind(SysRoleMenu.Fields.menuId, id)
                                .fetch()
                                .rowsUpdated()
                                .thenReturn(rowsUpdated)
                )
                .as(transaction -> transaction.as(transactionalOperator::transactional));
    }

    @Override
    public Mono<BigInteger> save(SysMenu sysMenu) {

        return Mono.deferContextual(ctx->{
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            if(Objects.isNull(sysMenu.getId())){
                sysMenu.setCreateTime(LocalDateTime.now());
                sysMenu.setCreatorId(userId);
              return sysMenuRepository.save(sysMenu)
                        .map(SysMenu::getId);
            }
            sysMenu.setUpdaterId(userId);
            sysMenu.setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(SysMenu.class),
                    sysMenu,
                    SysMenu.Fields.id
            );
        });
    }

    @Override
    public Mono<BigInteger> updateById(SysMenu sysMenu) {
        sysMenu.setUpdateTime(LocalDateTime.now());
        return r2dbcUpdateHelper.updateIgnoreNull(
                        EntityTableNameUtils.getName(SysMenu.class),
                        sysMenu,
                        SysMenu.Fields.id
                );
    }

    @Override
    public Flux<SysMenu> findAll() {
        return sysMenuRepository.findAll();
    }
}
