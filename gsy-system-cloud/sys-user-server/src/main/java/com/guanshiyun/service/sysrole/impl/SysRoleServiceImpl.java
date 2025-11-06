package com.guanshiyun.service.sysrole.impl;

import cn.hutool.core.util.StrUtil;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.SqlConstRepository;
import com.guanshiyun.pageutil.PageNumSizeUtil;
import com.guanshiyun.repository.sysrole.SysRoleRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import com.guanshiyun.service.sysrole.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleRepository sysRoleRepository;
    private final R2dbcEntityTemplate template;
    private final StringBuilder sb;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;
    private final SysUserRoleRepository sysUserRoleRepository;

    //目前添加，就暂时返回全部数据
    @Override
    public Flux<SysRole> save(SysRole sysRole) {
        sysRole.setId(null);
        return sysRoleRepository.save(sysRole)
                .flatMapMany(sysRoleAdd ->
                        sysRoleRepository.findAll()
                );
    }

    @Override
    public Mono<PageResultT<List<SysRole>>> findPage(RequestPage<SysRole> requestPage) {
        requestPage = isRequestPageNUll(requestPage);
        // 前端没传 pageSize 时默认10条
        BigInteger pageNum = PageNumSizeUtil.pageNum(requestPage.getPageNum());
        int pageSize = PageNumSizeUtil.pageSize(requestPage.getPageSize());
        // 条件
        Criteria criteria = Criteria.empty();
        String name = requestPage.getCondition().getName();
        //不为空，模糊查询
        if (!StrUtil.isBlank(name)) {
            String sql = sb.append(SqlConstRepository.PERCENT)
                    .append(name)
                    .append(SqlConstRepository.PERCENT)
                    .toString();
            criteria = criteria.and(SqlConstRepository.USERNAME).like(sql);
            // 清空
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
                        SysRole.class)
                .map(SysRole::getId)
                .singleOrEmpty()
                .flatMap(lastId -> lastId == null ?
                        Mono.just(PageResultT.<List<SysRole>>builder()
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
                                        SysRole.class
                                )
                                .collectList()
                                .flatMap(sysRoles ->
                                        sysRoleRepository.count(
                                                )
                                                .map(total ->
                                                        PageResultT.<List<SysRole>>builder()
                                                                .total(total)
                                                                .rows(sysRoles)
                                                                .build()
                                                )
                                )
                );
    }

    @Override
    public Mono<Long> deleteRoleById(BigInteger id) {
        return databaseClient.sql("DELETE FROM sys_role WHERE id = :id")
                .bind(SqlConstRepository.ID, id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->
                        databaseClient.sql("delete from sys_role_menu where role_id = :id")
                                .bind(SqlConstRepository.ID, id)
                                .fetch()
                                .rowsUpdated()
                                .flatMap(rowsChildren ->
                                        databaseClient.sql("delete from sys_user_role where role_id = :id")
                                                .bind(SqlConstRepository.ID, id)
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
    public Flux<SysRole> findAllByUserId(BigInteger userId) {
        return sysUserRoleRepository.findRoleIdByUserId(userId)
                .collectList()
                .flatMapMany(sysRoleRepository::findAllById);
    }


    private RequestPage<SysRole> isRequestPageNUll(RequestPage<SysRole> requestPage) {
        return requestPage == null ?
                RequestPage.<SysRole>builder()
                        .pageNum(BigInteger.ZERO)
                        .pageSize(ConstNumber.INT_ZERO)
                        .condition(SysRole.builder().build())
                        .build() :
                requestPage;
    }
}
