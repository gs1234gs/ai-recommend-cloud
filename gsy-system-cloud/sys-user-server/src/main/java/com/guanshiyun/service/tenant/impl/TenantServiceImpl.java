package com.guanshiyun.service.tenant.impl;

import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.tenant.PageTenantVO;
import com.guanshiyun.controller.tenant.TenantVO;
import com.guanshiyun.repository.tenant.TenantRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.tenant.TenantService;
import com.guanshiyun.tenant.Tenant;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    private final TenantRepository tenantRepository;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<Long> save(Tenant tenant) {

        if(Objects.isNull(tenant.getId())){
            return tenantRepository.save(tenant)
                    .map(Tenant::getId);
        }
        return r2dbcUpdateHelper.updateIgnoreNull(EntityTableNameUtils.getName(Tenant.class)
        ,tenant,Tenant.Fields.id);
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return tenantRepository.deleteById(id)
                .then(Mono.fromCallable(() -> true));
    }

    @Override
    public Mono<PageResultT<List<TenantVO>>> findPage(RequestPage<PageTenantVO> requestPage) {
        RequestPage<PageTenantVO> pageTenantVORequestPage = PageUtils.pageValidation(requestPage, PageTenantVO.class);
        RequestPage<Tenant> request = BeanConvertUtil.toBean(pageTenantVORequestPage, Tenant.class);
        return ReactivePageQuery.of(
                r2dbcEntityTemplate,
                Tenant.class,
                request

        )
                .like(Tenant.Fields.name,request.getCondition().getName())
                .orderByDesc(BasePojo.Fields.createTime)
                .page()
                .map(page->BeanConvertUtil.toBean(
                        page,
                        TenantVO.class
                ));
    }

    @Override
    public Mono<TenantVO> findById(Long id) {
        return tenantRepository.findById(id)
                .map(t->BeanConvertUtil.toBean(t,TenantVO.class));
    }
}
