package com.guanshiyun.service.sysrole;

import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SysRoleService {
    /**
     * 为了提升用户体验，返回最新默认分页数据
     * */
   Flux<SysRole> save(SysRole sysRole);
   /**
    * 带条件的分页查询
    * */
   Mono<PageResultT<List<SysRole>>> findPage(RequestPage<SysRole> requestPage);

    Mono<Long> deleteRoleById(BigInteger id);

    Flux<SysRole> findAllByUserId(BigInteger userId);
}
