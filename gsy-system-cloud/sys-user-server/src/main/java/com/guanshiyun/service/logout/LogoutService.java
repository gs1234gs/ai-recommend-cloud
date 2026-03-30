package com.guanshiyun.service.logout;

import reactor.core.publisher.Mono;

public interface LogoutService {
    //登出
    Mono<Long> logout();
}
