package com.guanshiyun.service.sysuser;

import com.guanshiyun.controller.sysuser.vo.SysUserSaveVO;
import com.guanshiyun.controller.sysuser.vo.SysUserVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.userpojo.SysUser;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface SysUserService {

    Mono<PageResultT<List<SysUser>>> findPage(RequestPage<SysUser> requestPage);

    Mono<Long> deleteUserById(BigInteger id);

    Mono< Long> deleteUserByIds(Collection<BigInteger> ids);

    Mono<SysUserVO> findById(BigInteger id);

    Mono<BigInteger> updateUserById(SysUserVO sysUserVO);

    Mono<BigInteger> save(SysUserSaveVO sysUserSaveVO);
}
