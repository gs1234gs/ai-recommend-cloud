package com.guanshiyun.security.handler;

import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
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
public class LogoutSuccessHandler {
    private final ReactiveRedisUtil reactiveRedisUtil;
    private final MyLong myLong;

    public Mono<Long> onLogoutSuccess() {

        return Mono.deferContextual(ctx ->{
            if(!myLong.hasKey(ctx)){
                log.error("用户id为空 ：null");
                return Mono.empty();
            }
            Long id =  myLong.findUserId(ctx);
          return   reactiveRedisUtil.hDel(ConstClassNickName.REDIS_TOKEN_KEY, id.toString())
                    .then(reactiveRedisUtil.hDel(ConstClassNickName.REDIS_AUTHORITY_KEY, id.toString()));
                }

        );
    }
}
