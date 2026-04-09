package com.guanshiyun.repository.resetforget;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;



public interface ResetForgetRepository extends R2dbcRepository<SysUser, Long> {
    Mono<SysUser> findByUsername(String username);
}
