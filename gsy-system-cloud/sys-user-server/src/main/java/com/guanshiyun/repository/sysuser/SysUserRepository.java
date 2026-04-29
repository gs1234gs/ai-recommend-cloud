package com.guanshiyun.repository.sysuser;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


public interface SysUserRepository extends R2dbcRepository<SysUser, Long> {

    Mono<Long> deleteUserById(Long id);

}
