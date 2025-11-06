package com.guanshiyun.repository.signin;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface SignInUpRepository extends ReactiveCrudRepository<SysUser, BigInteger> {
    Mono<SysUser> findByUsername(String username);
}
