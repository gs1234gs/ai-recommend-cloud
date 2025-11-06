package com.guanshiyun.utils;

import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.math.BigInteger;
import java.util.function.Supplier;
@RequiredArgsConstructor
public class WebContextUtils {
    private final MyBigInteger myBigInteger;
    public  <T> Mono<T> withUserContextMono(Supplier<Mono<T>> remoteCall) {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger userId = myBigInteger.bigIntegerOrNull(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return remoteCall.get()
                        .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId));
            }
            return remoteCall.get();
        });
    }

    public  <T> Mono<T> withUserContextMono(Supplier<Mono<T>> remoteCall,boolean isNeedUserId) {
        return Mono.deferContextual(ctx -> {
            if ( isNeedUserId) {
                if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                    return Mono.error(new RuntimeException("用户未登录"));
                }
                BigInteger userId = myBigInteger.bigIntegerOrNull(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return remoteCall.get()
                        .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId));
            }
            return remoteCall.get();
        });
    }
    public  <T> Flux<T> withUserContextFlux(Supplier<Flux<T>> remoteCall) {
        return Flux.deferContextual(ctx -> {
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger userId = myBigInteger.bigIntegerOrNull(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return remoteCall.get()
                        .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId));
            }
            return remoteCall.get();
        });
    }
    public  <T> Flux<T> withUserContextFlux(Supplier<Flux<T>> remoteCall,boolean isNeedUserId) {
        return Flux.deferContextual(ctx -> {
            if ( isNeedUserId) {
                if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                    return Mono.error(new RuntimeException("用户未登录"));
                }
                BigInteger userId = myBigInteger.bigIntegerOrNull(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return remoteCall.get()
                        .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId));
            }
            return remoteCall.get();
        });
    }

}
