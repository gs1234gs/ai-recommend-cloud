package com.guanshiyun.service.logout.impl;

import com.guanshiyun.security.handler.RewriteLogoutSuccessHandler;
import com.guanshiyun.service.logout.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService {
    private final RewriteLogoutSuccessHandler rewriteLogoutSuccessHandler;

    @Override
    public Mono<Long> logout() {
        return rewriteLogoutSuccessHandler.onLogoutSuccess();
    }
}
