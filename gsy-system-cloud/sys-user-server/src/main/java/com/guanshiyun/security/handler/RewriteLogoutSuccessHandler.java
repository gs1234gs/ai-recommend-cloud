package com.guanshiyun.security.handler;

import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



/**
 * 处理登录退出的LogoutSuccessHandler实现类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RewriteLogoutSuccessHandler {
    private final ReactiveRedisUtil reactiveRedisUtil;

    public Mono<Long> onLogoutSuccess() {

        return Mono.deferContextual(ctx ->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                log.error("用户id为空 ：null");
                return Mono.empty();
            }
            Long id =  ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
          return   reactiveRedisUtil.hDel(ConstClassNickName.REDIS_TOKEN_KEY, id.toString())
                    .then(reactiveRedisUtil.hDel(ConstClassNickName.REDIS_AUTHORITY_KEY, id.toString()));
                }

        );
    }
}
