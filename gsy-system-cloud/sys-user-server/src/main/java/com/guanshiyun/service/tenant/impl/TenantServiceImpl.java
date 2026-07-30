package com.guanshiyun.service.tenant.impl;

import com.db.cursorQuery.ReactiveQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.tenant.vo.PageTenantVO;
import com.guanshiyun.controller.tenant.vo.TenantVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.tenant.TenantRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.tenant.TenantService;
import com.guanshiyun.tenant.SysTenant;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    private final TenantRepository tenantRepository;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final MyLong myLong;
    private final ReactiveQuery reactiveQuery;

    @Override
    public Mono<Long> save(SysTenant tenant) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new Throwable("登陆已经过期"));
            }
            Long userId = myLong.findUserId(ctx);
            LocalDateTime now = LocalDateTime.now();
            if (Objects.isNull(tenant.getId())) {
                tenant.setCreator(userId)
                        .setCreateTime(now);
                return tenantRepository.save(tenant)
                        .map(SysTenant::getId);
            }
            tenant.setUpdater(userId)
                    .setUpdateTime(now);
            return r2dbcUpdateHelper.updateIgnoreNull(
                    SysTenant.class,
                    tenant,
                    SysTenant.Fields.id
            );
        });

    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return tenantRepository.deleteById(id)
                .then(Mono.fromCallable(() -> true));
    }

    @Override
    public Mono<PageResultT<List<TenantVO>>> findPage(RequestPage<PageTenantVO> requestPage) {
        RequestPage<PageTenantVO> pageTenantVORequestPage = PageUtils.pageValidation(requestPage, PageTenantVO.class);
        RequestPage<SysTenant> request = BeanConvertUtil.toBean(pageTenantVORequestPage, SysTenant.class);
        return reactiveQuery.createQuery(SysTenant.class, request)
                .like(SysTenant.Fields.name, request.getCondition().getName())
                .orderByDesc(BasePojo.Fields.createTime)
                .page()
                .map(page -> BeanConvertUtil.toBean(
                        page,
                        TenantVO.class
                ));
    }

    @Override
    public Mono<TenantVO> findById(Long id) {
        return tenantRepository.findById(id)
                .map(t -> BeanConvertUtil.toBean(t, TenantVO.class));
    }
}
