package com.guanshiyun.controller.tenant;

import com.guanshiyun.controller.tenant.vo.PageTenantVO;
import com.guanshiyun.controller.tenant.vo.TenantSaveVO;
import com.guanshiyun.controller.tenant.vo.TenantVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.tenant.TenantService;
import com.guanshiyun.tenant.SysTenant;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tenant/")
public class TenantController {
    private final TenantService tenantService;

    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody TenantSaveVO tenantSaveVO){
        return tenantService.save(BeanConvertUtil.toBean(tenantSaveVO, SysTenant.class))
                .map(ResultT::success);
    }

    //删除
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Boolean>> deleteById(@PathVariable Long id){
        return tenantService.delete(id)
                .map(ResultT::success);
    }

    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<TenantVO>>>> findPage(@RequestBody RequestPage<PageTenantVO> requestPage){
        return tenantService.findPage(requestPage)
                .map(ResultT::success);
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<TenantVO>> findById(@PathVariable Long id){
        return tenantService.findById(id)
                .map(ResultT::success);
    }


}
