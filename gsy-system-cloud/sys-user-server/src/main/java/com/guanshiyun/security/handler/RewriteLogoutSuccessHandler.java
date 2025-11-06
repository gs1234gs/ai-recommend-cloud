package com.guanshiyun.security.handler;

import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 处理登录退出的LogoutSuccessHandler实现类
 */
@Component
@RequiredArgsConstructor
public class RewriteLogoutSuccessHandler  {
    private final ReactiveRedisUtil reactiveRedisUtil;

    public Mono<Long> onLogoutSuccess() {

        //删除token
        String id = "1";

        return reactiveRedisUtil.hDel(
                        ConstClassNickName.REDIS_TOKEN_KEY,
                        id
                );
    }
}
