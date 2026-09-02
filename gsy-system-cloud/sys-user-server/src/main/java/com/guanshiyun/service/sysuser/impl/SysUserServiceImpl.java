package com.guanshiyun.service.sysuser.impl;

import com.db.cursorQuery.ReactiveQuery;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.controller.sysuser.vo.SysUserSaveVO;
import com.guanshiyun.controller.sysuser.vo.SysUserVO;
import com.guanshiyun.controller.tenant.vo.TenantVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.sysrole.SysRoleRepository;
import com.guanshiyun.repository.sysuser.SysUserRepository;
import com.guanshiyun.repository.tenant.TenantRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import com.guanshiyun.service.sysuser.SysUserService;
import com.guanshiyun.tenant.SysTenant;
import com.guanshiyun.userpojo.SysUser;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final TransactionalOperator transactionalOperator;
    private final DatabaseClient databaseClient;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final MyLong myLong;
    private final SysRoleRepository sysRoleRepository;
    private final TenantRepository tenantRepository;
    private final ReactiveQuery reactiveQuery;

    /**
     * Keyset分页查询SysUser
     * <p>
     * pageSize 页大小
     * username 可选查询条件，模糊查询
     *
     * @return Flux<SysUser>
     */
    @Override
    public Mono<PageResultT<List<SysUserVO>>> findPage(RequestPage<SysUserVO> requestPage) {
        RequestPage<SysUser> sysUserRequestPage = BeanConvertUtil.toBean(requestPage, SysUser.class);
        return reactiveQuery.createQuery(SysUser.class, sysUserRequestPage)
                .orderByDesc(SysUser.Fields.id)
                .like(SysUser.Fields.username, requestPage.getCondition().getUsername())
                .like(SysUser.Fields.nickName, requestPage.getCondition().getNickName())
                .orderByDesc(BasePojo.Fields.createTime)
                .page()
                .map(page -> BeanConvertUtil.toBean(page, SysUserVO.class));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return Integer
     */
    @Override
    public Mono<Long> deleteUserById(Long id) {
//        return databaseClient.sql("delete from sys_user where id = :id")
//                .bind(SysUser.Fields.id, id)
//                .fetch()
//                .rowsUpdated()
        return sysUserRepository.deleteUserById(id)
                .flatMap(rowsUpdated ->
                                sysUserRoleRepository.deleteSysUserRoleByUserId(id)
                                        .thenReturn(rowsUpdated)
//                        databaseClient.sql("delete from sys_user_role where user_id = :id")
//                                .bind(SysUserRole.Fields.id, id)
//                                .fetch()
//                                .rowsUpdated()
//                                .thenReturn(rowsUpdated)
                ).transform(transactionalOperator::transactional);
    }

    @Override
    public Mono<Long> deleteUserByIds(Collection<Long> ids) {
        return databaseClient.sql("delete from sys_user where id in (:id)")
                .bind(SysUser.Fields.id, ids)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->
                        databaseClient.sql("delete from sys_user_role where user_id in (:userId)")
                                .bind(SysUserRole.Fields.userId, ids)
                                .fetch()
                                .rowsUpdated()
                                .thenReturn(rowsUpdated)
                )
                .transform(transactionalOperator::transactional);

    }

    @Override
    public Mono<SysUserVO> findById(Long id) {
//       return Mono.deferContextual(ctx ->{
//           Long userId = myLong.Long(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
//           return sysUserRepository.findById(userId);
//       });
        Mono<List<Long>> roleIdMono = sysUserRoleRepository.findRoleIdByUserId(id).collectList();
        Mono<SysUser> userMono = sysUserRepository.findById(id);
        return Mono.zip(roleIdMono, userMono)
                .flatMap(tuple -> {
                    List<Long> roleIds = tuple.getT1();
                    SysUser sysUser = tuple.getT2();

                    SysUserVO sysUserVO = BeanConvertUtil.toBean(sysUser, SysUserVO.class);

                    Mono<SysTenant> tanantMono = tenantRepository.findById(sysUser.getTenantId());

                    Mono<List<SysRole>> sysRoleMono = sysRoleRepository.findAllById(roleIds)
                            .collectList();
                    return Mono.zip(tanantMono, sysRoleMono)
                            .map(tuple2 -> {
                                SysTenant t1 = tuple2.getT1();
                                List<SysRole> t2 = tuple2.getT2();
                                List<SysRoleVO> sysRoleVOS = BeanConvertUtil.toBeanList(t2, SysRoleVO.class);
                                return sysUserVO.setRoles(sysRoleVOS).setTenant(BeanConvertUtil.toBean(t1, TenantVO.class))
                                        .setPassword(null);
                            });
                });
    }

    @Override
    public Mono<Long> updateUserById(SysUserVO sysUser) {
        String password = sysUser.getPassword();
        if (Objects.nonNull(password)) {
            sysUser.setPassword(passwordEncoder.encode(password));
        }
        return sysUserRepository.findById(sysUser.getId())
                .flatMap(sysUserDB -> {
                            BeanConvertUtil.toBean(sysUser, sysUserDB);
                            sysUserDB.setUpdateTime(LocalDateTime.now());
                            return r2dbcUpdateHelper.updateIgnoreNull(
                                            SysUser.class,
                                            sysUserDB,
                                            SysUser.Fields.id
                                    )
                                    .flatMap(id ->
                                            sysUserRoleRepository
                                                    .findExistsUserRole(id, sysUser.getRoles().stream().map(SysRoleVO::getId)
                                                            .toList())
                                                    .then(Mono.just(id))

                                    )
                                    .transform(transactionalOperator::transactional);
                        }
                );
    }

    @Override
    public Mono<Long> save(SysUserSaveVO sysUserSaveVO) {
        final String[] password = {sysUserSaveVO.getPassword()};
        LocalDateTime now = LocalDateTime.now();
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx))
                return Mono.error(new Exception("用户ID不存在"));
            Long userId =
                    myLong.findUserId(ctx);
            SysUser sysUser = BeanConvertUtil.toBean(sysUserSaveVO, SysUser.class);
            //id为空，表示新增
            if (Objects.isNull(sysUser.getId())) {
                sysUser.setCreator(userId);
                sysUser.setCreateTime(now);
                //密码为空，默认密码为123456
                if (Objects.isNull(password[0])) {
                    password[0] = "123456";
                }
                sysUser.setPassword(passwordEncoder.encode(password[0]));
                return sysUserRepository.save(sysUser)
                        .flatMap(sysUserSave ->
                                sysUserRoleRepository.saveAll(
                                                sysUserSaveVO.getRoleIdList().stream()
                                                        .map(roleId -> SysUserRole.builder()
                                                                .id(null)
                                                                .roleId(roleId)
                                                                .userId(sysUserSave.getId())
                                                                .build())
                                                        .toList()
                                        )
                                        .collectList()
                                        .onErrorResume(e -> Mono.error(new RuntimeException("保存用户角色失败")))
                                        .then(Mono.just(sysUserSave.getId()))
                        )
                        .onErrorResume(e -> {
                            log.error("保存用户失败：{}", e.getMessage());
                            return Mono.error(new RuntimeException("保存用户失败"));
                        })
                        .transform(transactionalOperator::transactional);
            }
            //id不为空，表示修改
            sysUser.setUpdater(userId);
            sysUser.setUpdateTime(now);
            //密码不为空，表示修改密码，否则不修改
            if (Objects.nonNull(password[0])) {
                sysUser.setPassword(passwordEncoder.encode(password[0]));
            }
            return r2dbcUpdateHelper.updateIgnoreNull(
                            SysUser.class,
                            sysUser,
                            SysUser.Fields.id
                    ).flatMap(id -> {
                        //删除所有角色
                        return sysUserRoleRepository.deleteAllByUserId(id)
                                //重新插入新的的角色
                                .thenMany(Flux.fromIterable(sysUserSaveVO.getRoleIdList()))
                                .flatMap(roleId -> sysUserRoleRepository.save(
                                        SysUserRole.builder()
                                                .id(null)
                                                .userId(id)
                                                .roleId(roleId)
                                                .build()))
                                .then(Mono.just(id));
                    })
                    .transform(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<SysUserVO> updateSignInUser(SysUserSaveVO sysUserSaveVO) {
        SysUser sysUser = BeanConvertUtil.toBean(sysUserSaveVO, SysUser.class);
        LocalDateTime now = LocalDateTime.now();
        sysUser.setUpdateTime(now)
                .setUpdateTime(now);
        String password = sysUserSaveVO.getPassword();
        if (Objects.nonNull(password)) {
            sysUser.setPassword(passwordEncoder.encode(password));
        }
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("请先登录"));
            }
            Long id = myLong.findUserId(ctx);
            sysUser.setId(id);
            return r2dbcUpdateHelper.updateIgnoreNull(
                            SysUser.class,
                            sysUser,
                            SysUser.Fields.id
                    )
                    .flatMap(sysUserRepository::findById)
                    .map(user -> {
                        return BeanConvertUtil.toBean(user, SysUserVO.class);
                    });
        });
    }

    @Override
    public Mono<SysUserVO> findById() {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("请先登录"));
            }
            Long id = myLong.findUserId(ctx);
            Mono<List<Long>> roleIdMono = sysUserRoleRepository.findRoleIdByUserId(id).collectList();
            Mono<SysUser> userMono = sysUserRepository.findById(id);
            return Mono.zip(roleIdMono, userMono)
                    .flatMap(tuple -> {
                        List<Long> roleIds = tuple.getT1();
                        SysUser sysUser = tuple.getT2();
                        SysUserVO sysUserVO = BeanConvertUtil.toBean(sysUser, SysUserVO.class);
                        Mono<SysTenant> tenantMono = tenantRepository.findById(sysUser.getTenantId());
                        Mono<List<SysRole>> sysRolesMono = sysRoleRepository.findAllById(roleIds)
                                .collectList();
                        return Mono.zip(tenantMono, sysRolesMono)
                                .map(tuple2 -> {
                                    SysTenant sysTenant = tuple2.getT1();
                                    List<SysRole> sysRoleList = tuple2.getT2();
                                    List<SysRoleVO> sysRoleVOS = BeanConvertUtil.toBeanList(sysRoleList, SysRoleVO.class);
                                    return sysUserVO.setRoles(sysRoleVOS)
                                            .setTenant(BeanConvertUtil.toBean(sysTenant, TenantVO.class))
                                            .setPassword(null);
                                });
                    });
        });
    }
}
