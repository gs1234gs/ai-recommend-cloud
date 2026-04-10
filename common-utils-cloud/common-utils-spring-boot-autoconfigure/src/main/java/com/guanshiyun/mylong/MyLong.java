package com.guanshiyun.mylong;

import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;


@Slf4j
public class MyLong {

    public Long myLong(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.parseLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常，number：{}", number);
            throw new RuntimeException("转换Long异常", e);
        }
        return mylong;
    }
    //重载，允许返回null
    public Long longOrNull(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.parseLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常", e);
        }
        return mylong;
    }
    public Long findUserId(ContextView contextView) {
        return  myLong(contextView.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
    }

    public Long findTenantId(ContextView contextView) {
        return  myLong(contextView.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY));
    }

    public Boolean hasKey(ContextView contextView) {
        return contextView.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
    }
    public Mono<Boolean> hasKey(){
        return Mono.deferContextual(ctx->{
            return Mono.just(ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY));
        });
    }

    public Mono<Long> findUserId() {
        return Mono.deferContextual(ctx->{
            Boolean hasKey = hasKey(ctx);
            if(!hasKey){
                return Mono.error(new Throwable("登陆失效"));
            }
          return   Mono.just(findUserId(ctx));
        });

    }
    public Mono<Long> findTenantId() {
       return Mono.deferContextual(ctx->{
            Boolean hasKey = hasKey(ctx);
            if(!hasKey){
                return Mono.error(new Throwable("租户不存在"));
            }
            return   Mono.just(findTenantId(ctx));
        });
    }

    public Mono<Long> findTenantIdOrNotExit() {
        return Mono.deferContextual(ctx->{
            Boolean hasKey = hasKey(ctx);
            if(!hasKey){
                return Mono.just(-1L);
            }
            return   Mono.just(findTenantId(ctx));
        });
    }

    public Mono<Long> findUserIdOrNotExit() {
        return Mono.deferContextual(ctx -> {
            Boolean hasKey = hasKey(ctx);
            if (!hasKey) {
                return Mono.just(-1L);
            }
            return Mono.just(findUserId(ctx));
        });
    }
}
