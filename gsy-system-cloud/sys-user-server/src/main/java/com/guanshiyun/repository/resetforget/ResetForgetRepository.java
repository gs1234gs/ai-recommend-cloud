package com.guanshiyun.repository.resetforget;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;



public interface ResetForgetRepository extends ReactiveCrudRepository<SysUser, Long> {
    Mono<SysUser> findByUsername(String username);
}
