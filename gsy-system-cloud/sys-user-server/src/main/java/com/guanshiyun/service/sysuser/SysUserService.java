package com.guanshiyun.service.sysuser;

import com.guanshiyun.controller.sysuser.vo.SysUserSaveVO;
import com.guanshiyun.controller.sysuser.vo.SysUserVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.userpojo.SysUser;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface SysUserService {

    Mono<PageResultT<List<SysUser>>> findPage(RequestPage<SysUser> requestPage);

    Mono<Long> deleteUserById(Long id);

    Mono< Long> deleteUserByIds(Collection<Long> ids);

    Mono<SysUserVO> findById(Long id);

    Mono<Long> updateUserById(SysUserVO sysUserVO);

    Mono<Long> save(SysUserSaveVO sysUserSaveVO);

    Mono<SysUserVO> updateSignInUser(SysUserSaveVO sysUserSaveVO);

    Mono<SysUserVO> findById();
}
