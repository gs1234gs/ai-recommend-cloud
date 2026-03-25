package com.guanshiyun.service.sysrole;

import com.guanshiyun.controller.sysrole.vo.SysRoleSaveVO;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rolepojo.SysRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface SysRoleService {
    /**
     * 为了提升用户体验，返回最新默认分页数据
     * */
   Mono<Long> save(SysRoleSaveVO sysRoleSaveVO);
   /**
    * 带条件的分页查询
    * */
   Mono<PageResultT<List<SysRoleVO>>> findPage(RequestPage<SysRoleVO> requestPage);

    Mono<Long> deleteRoleById(Long id);

    Flux<SysRole> findAllByUserId(Long userId);

    Mono<SysRoleVO> findById(Long id);
    Mono<Long> update(SysRoleSaveVO sysRoleSaveVO);
}
