package com.guanshiyun.security.handler;

import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

/**
 * 处理登录退出的LogoutSuccessHandler实现类
 */
@Component
@RequiredArgsConstructor
public class RewriteLogoutSuccessHandler {
    private final ReactiveRedisUtil reactiveRedisUtil;

    public Mono<Long> onLogoutSuccess() {

        return Mono.deferContextual(ctx ->{
            BigInteger id =  ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
          return   reactiveRedisUtil.hDel(ConstClassNickName.REDIS_TOKEN_KEY, id.toString())
                    .then(reactiveRedisUtil.hDel(ConstClassNickName.REDIS_AUTHORITY_KEY, id.toString()));
                }

        );
    }
}
