//package com.guanshiyun.base;
//
//import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.ReactiveAuditorAware;
//import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
//import reactor.core.publisher.Mono;
//
//@Configuration(proxyBeanMethods = false)
//@EnableR2dbcAuditing
//public class EnableR2dbcAuditingConfig {
//
//    @Bean
//    public ReactiveAuditorAware<Object> auditorAware() {
//        // 重点：() -> Mono<?> 实现函数接口
//        return () -> Mono.deferContextual(ctx -> {
//            if (ctx.isEmpty() || !ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
//                // 无用户 → 返回空，不填充审计字段
//                return Mono.empty();
//            }
//            Object userIdObj = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
//            try {
//                return Mono.just(Long.valueOf(userIdObj.toString()));
//            } catch (NumberFormatException e) {
//                return Mono.just(0L);
//            }
//        });
//    }
//}
