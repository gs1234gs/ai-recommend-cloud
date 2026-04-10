package com.guanshiyun.service.logout.impl;

import com.guanshiyun.security.handler.LogoutSuccessHandler;
import com.guanshiyun.service.logout.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService {
    private final LogoutSuccessHandler logoutSuccessHandler;

    @Override
    public Mono<Long> logout() {
        return logoutSuccessHandler.onLogoutSuccess();
    }
}
