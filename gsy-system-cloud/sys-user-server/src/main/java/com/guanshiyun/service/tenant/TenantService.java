package com.guanshiyun.service.tenant;

import com.guanshiyun.controller.tenant.vo.PageTenantVO;
import com.guanshiyun.controller.tenant.vo.TenantVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.tenant.SysTenant;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TenantService {
    //添加或者修改租户
    Mono<Long> save(SysTenant tenant);
    //删除
    Mono<Boolean> delete(Long id);
    //分页查询
    Mono<PageResultT<List<TenantVO>>> findPage(RequestPage<PageTenantVO> requestPage);

   Mono<TenantVO> findById(Long id);
}
