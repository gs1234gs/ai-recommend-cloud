package com.guanshiyun.service.sysuser.impl;

import cn.hutool.core.util.StrUtil;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.SqlConstRepository;
import com.guanshiyun.pageutil.PageNumSizeUtil;
import com.guanshiyun.relationpojo.SysUserRole;
import com.guanshiyun.repository.sysuser.SysUserRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.roleId.RoleIdConst;
import com.guanshiyun.service.sysuser.SysUserService;
import com.guanshiyun.userpojo.SysUser;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserRepository sysUserRepository;
    private final R2dbcEntityTemplate template;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final StringBuilder sb;
    private final TransactionalOperator transactionalOperator;
    private final DatabaseClient databaseClient;
    private final MyBigInteger myBigInteger;

    /**
     * Keyset分页查询SysUser
     * <p>
     * lastId   上一页最后一条ID，第一次传null表示第一页
     * pageSize 页大小
     * username 可选查询条件，模糊查询
     *
     * @return Flux<SysUser>
     */
    @Override
    public Mono<PageResultT<List<SysUser>>> findPage(RequestPage<SysUser> requestPage) {
        requestPage = isRequestPageNUll(requestPage);
        // 前端没传 pageSize 时默认10条
        BigInteger pageNum = PageNumSizeUtil.pageNum(requestPage.getPageNum());
        int pageSize = PageNumSizeUtil.pageSize(requestPage.getPageSize());
        // 条件
        Criteria criteria = Criteria.empty();
        String username = requestPage.getCondition().getUsername();
        String nickName = requestPage.getCondition().getNickName();
        //拼接sql
        // 用户名不为 空，模糊查询
        if (!StrUtil.isBlank(username)) {
            String sql = sb.append(SqlConstRepository.PERCENT)
                    .append(username)
                    .append(SqlConstRepository.PERCENT)
                    .toString();
            criteria = criteria.and(SqlConstRepository.USERNAME).like(sql);
            // 清空
            sb.delete(ConstNumber.INT_ZERO, sb.length());
        }
        // 昵称不为 空，模糊查询
        if (!StrUtil.isBlank(nickName)) {
            String sql = sb.append(SqlConstRepository.PERCENT)
                    .append(nickName)
                    .append(SqlConstRepository.PERCENT)
                    .toString();
            criteria = criteria.and(SqlConstRepository.NICK_NAME).like(sql);
            sb.delete(ConstNumber.INT_ZERO, sb.length());
        }
        BigInteger offset = BigInteger.ZERO;
        final Criteria finalCriteria = criteria;
        // 最后一条ID
        if (pageNum != null &&
                pageNum.compareTo(BigInteger.ZERO) > ConstNumber.INTEGER_ZERO) {
            //计算起始条数
            offset = pageNum.subtract(BigInteger.ONE).multiply(BigInteger.valueOf(pageSize));
        }
        return template.select(Query.query(criteria)
                                .sort(Sort.by(Sort.Order.desc(SqlConstRepository.ID)))
                                .offset(offset.longValue())
                                .limit(ConstNumber.INT_ONE),
                        SysUser.class)
                .map(SysUser::getId)
                .singleOrEmpty()
                .flatMap(lastId -> lastId == null ?
                        Mono.just(PageResultT.<List<SysUser>>builder()
                                .total(0)
                                .rows(Collections.emptyList())
                                .build()) :
                        template.select(
                                        Query.query(
                                                        finalCriteria.and(SqlConstRepository.ID)
                                                                .lessThanOrEquals(lastId)
                                                )
                                                .sort(Sort.by(Sort.Order.desc(SqlConstRepository.ID)))
                                                .limit(pageSize),
                                        SysUser.class
                                )
                                .collectList()
                                .flatMap(sysUsers ->
                                        sysUserRepository.count(
                                                )
                                                .map(total ->
                                                        PageResultT.<List<SysUser>>builder()
                                                                .total(total)
                                                                .rows(sysUsers)
                                                                .build()
                                                )
                                )
                );
    }

    /**
     * 删除用户
     *
     * @param id
     * @return Integer
     */
    @Transactional
    @Override
    public Mono<Long> deleteUserById(BigInteger id) {
        return transactionalOperator.execute(status ->
                        databaseClient.sql("delete from sys_user where id = :id")
                                .bind(SqlConstRepository.ID, id)
                                .fetch()
                                .rowsUpdated()
                                .flatMap(rowsUpdated ->
                                        databaseClient.sql("delete from sys_user_role where user_id = :id")
                                                .bind(SqlConstRepository.ID, id)
                                                .fetch()
                                                .rowsUpdated()
                                                .thenReturn(rowsUpdated)
                                ))
                .single();
    }

    @Override
    public Mono<Long> deleteUserByIds(Collection<BigInteger> ids) {
        return databaseClient.sql("delete from sys_user where id in (:ids)")
                .bind(SqlConstRepository.IDS, ids)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->
                        databaseClient.sql("delete from sys_user_role where user_id in (:ids)")
                                .bind(SqlConstRepository.IDS, ids)
                                .fetch()
                                .rowsUpdated()
                                .thenReturn(rowsUpdated)
                )
                .as(transaction ->
                        transaction.as(transactionalOperator::transactional));

    }

    @Override
    public Mono<SysUser> findById(BigInteger id) {
//       return Mono.deferContextual(ctx ->{
//           BigInteger userId = myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
//           return sysUserRepository.findById(userId);
//       });
        return sysUserRepository.findById(id);
    }

    @Override
    public Mono<SysUser> updateUserById(SysUser sysUser) {
        return sysUserRepository.findById(sysUser.getId())
                .flatMap(sysUserDB -> {
                            BeanConvertUtil.copyNonNullToTarget(sysUser, sysUserDB);
                            sysUserDB.setUpdateTime(LocalDateTime.now());
                            return sysUserRepository.save(sysUserDB);
                        }
                );
    }

    @Override
    public Mono<SysUser> save(SysUser sysUser) {
        String password = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setId(null);
        sysUser.setPassword(password);
        sysUser.setCreateTime(LocalDateTime.now());
        return sysUserRepository.save(sysUser)
                .flatMap(sysUserSave ->
                        sysUserRoleRepository.save(SysUserRole.builder()
                                        .id(null)
                                        .roleId(RoleIdConst.ROLE_COMMON_USER)
                                        .userId(sysUserSave.getId())
                                        .build())
                                .flatMap(sysUserRole -> Mono.just(sysUserSave))
                );
    }

    private RequestPage<SysUser> isRequestPageNUll(RequestPage<SysUser> requestPage) {
        return requestPage == null ?
                RequestPage.<SysUser>builder()
                        .pageNum(BigInteger.ZERO)
                        .pageSize(ConstNumber.INT_ZERO)
                        .condition(SysUser.builder().build())
                        .build() :
                requestPage;
    }
}
