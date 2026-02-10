package com.guanshiyun.service.sysuser.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.db.constsql.SqlConst;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.sysuser.vo.SysUserSaveVO;
import com.guanshiyun.controller.sysuser.vo.SysUserVO;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.sysuser.SysUserRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.sysuser.SysUserService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserRepository sysUserRepository;
    private final R2dbcEntityTemplate template;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final TransactionalOperator transactionalOperator;
    private final DatabaseClient databaseClient;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final MyBigInteger myBigInteger;

    /**
     * Keyset分页查询SysUser
     * <p>
     * pageSize 页大小
     * username 可选查询条件，模糊查询
     *
     * @return Flux<SysUser>
     */
    @Override
    public Mono<PageResultT<List<SysUser>>> findPage(RequestPage<SysUser> requestPage) {
        requestPage = PageUtils.pageValidation(requestPage, SysUser.class);
        // 前端没传 pageSize 时默认10条
        BigInteger pageNum = requestPage.getPageNum();
        int pageSize = requestPage.getPageSize();
        // 条件
        Criteria criteria = Criteria.empty();
        String username = requestPage.getCondition().getUsername();
        String nickName = requestPage.getCondition().getNickName();
        // 用户名不为 空，模糊查询
        if (StrUtil.isNotBlank(username))
            criteria = criteria.and(SysUser.Fields.username).like(SqlConst.PERCENT + username + SqlConst.PERCENT);
        // 昵称不为 空，模糊查询
        if (StrUtil.isNotBlank(nickName))
            criteria = criteria.and(
                    SysUser.Fields.nickName
            ).like(SqlConst.PERCENT + nickName + SqlConst.PERCENT);
        // 计算 offset
        long offset = pageNum.subtract(BigInteger.ONE)
                .multiply(BigInteger.valueOf(pageSize))
                .longValue();
        // 数据查询：ORDER BY id DESC（推荐主键排序）
        Query dataQuery = Query.query(criteria)
                .sort(Sort.by(
                        Sort.Order.desc(SysUser.Fields.createTime),
                        Sort.Order.desc(SysUser.Fields.id))) // 推荐用 id 排序
                .offset(offset)
                .limit(pageSize);
        // 总数查询
        Query countQuery = Query.query(criteria);
        return template.select(countQuery, SysUser.class)
                .count() // 执行 COUNT(*)
                .flatMap(count -> template.select(dataQuery, SysUser.class)
                        .collectList() // 查询分页数据
                        .map(dataList -> PageResultT.<List<SysUser>>builder()
                                .total(count)
                                .rows(dataList)
                                .build()
                        )
                );
    }

    /**
     * 删除用户
     *
     * @param id
     * @return Integer
     */
    @Override
    public Mono<Long> deleteUserById(BigInteger id) {
        return transactionalOperator.execute(status ->
                        databaseClient.sql("delete from sys_user where id = :id")
                                .bind(SysUser.Fields.id, id)
                                .fetch()
                                .rowsUpdated()
                                .flatMap(rowsUpdated ->
                                        databaseClient.sql("delete from sys_user_role where user_id = :id")
                                                .bind(SysUserRole.Fields.id, id)
                                                .fetch()
                                                .rowsUpdated()
                                                .thenReturn(rowsUpdated)
                                ))
                .single();
    }

    @Override
    public Mono<Long> deleteUserByIds(Collection<BigInteger> ids) {
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
                .as(transaction ->
                        transaction.as(transactionalOperator::transactional));

    }

    @Override
    public Mono<SysUserVO> findById(BigInteger id) {
//       return Mono.deferContextual(ctx ->{
//           BigInteger userId = myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
//           return sysUserRepository.findById(userId);
//       });
        return sysUserRepository.findById(id)
                .map(SysUser -> BeanUtil.toBean(SysUser, SysUserVO.class));
    }

    @Override
    public Mono<BigInteger> updateUserById(SysUserVO sysUser) {
        return sysUserRepository.findById(sysUser.getId())
                .flatMap(sysUserDB -> {
                    BeanUtil.copyProperties(sysUser, sysUserDB, CopyOptions.create().ignoreNullValue());
                            sysUserDB.setUpdateTime(LocalDateTime.now());
                            return r2dbcUpdateHelper.updateIgnoreNull(
                                            EntityTableNameUtils.getName(SysUser.class),
                                            sysUserDB,
                                            SysUser.Fields.id
                                    )
                                    .flatMap(id ->
                                            sysUserRoleRepository
                                                    .findExistsUserRole(id, sysUser.getRoleId())
                                                    .then(Mono.just(id))

                                    )
                                    .transform(transactionalOperator::transactional);
                        }
                );
    }

    @Override
    public Mono<BigInteger> save(SysUserSaveVO sysUserSaveVO) {
        String password = passwordEncoder.encode(sysUserSaveVO.getPassword());
        sysUserSaveVO.setPassword(password);
        sysUserSaveVO.setCreateTime(LocalDateTime.now());
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.error(new Exception("用户ID不存在"));
            BigInteger userId =
                    myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            SysUser sysUser = BeanUtil.toBean(sysUserSaveVO, SysUser.class);
            if (Objects.isNull(sysUserSaveVO.getId())) {
                sysUser.setCreatorId(userId);
                sysUser.setCreateTime(LocalDateTime.now());
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
            sysUser.setUpdaterId(userId);
            sysUser.setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(SysUser.class),
                    sysUser,
                    SysUser.Fields.id
            ).flatMap(id -> {
                return sysUserRoleRepository.deleteAllByUserId(id)
                        //重新插入新的的角色
                        .thenMany(Flux.fromIterable(sysUserSaveVO.getRoleIdList()))
                        .flatMap(roleId -> sysUserRoleRepository.save(SysUserRole.builder()
                                .id(null)
                                .userId(id)
                                .roleId(roleId)
                                .build()))
                        .then(Mono.just(id));
            });
        });
    }
}
