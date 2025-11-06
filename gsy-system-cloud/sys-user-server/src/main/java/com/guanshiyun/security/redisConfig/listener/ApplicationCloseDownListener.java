//package com.guanshiyun.security.redisConfig.listener;
//
//
//import com.guanshiyun.consts.ConstClassNickName;
//import jakarta.annotation.Nonnull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextClosedEvent;
//import org.springframework.data.redis.core.ReactiveRedisTemplate;
//import org.springframework.stereotype.Component;
//
///**
// * 关闭项目监听器，Spring 容器关闭时触发
// * 该类实现了 Spring 的 ApplicationListener 接口，用于监听 ContextClosedEvent 事件。
// * 当 Spring 容器关闭时（例如应用正常关闭或 JVM 停止），会触发该监听器。
// */
//@Slf4j
//@Component
//public class ApplicationCloseDownListener implements ApplicationListener<ContextClosedEvent> {
//
//    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
//
//    public ApplicationCloseDownListener(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
//        this.reactiveRedisTemplate = reactiveRedisTemplate;
//    }
//
//    @Override
//    public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
//        log.info("项目关闭，响应式清理 Redis 缓存...");
//        reactiveRedisTemplate.delete(ConstClassNickName.REDIS_TOKEN_KEY)
//                .doOnSuccess(count -> log.info("Redis 缓存清理完成，删除键数量：{}", count))
//                .doOnError(e -> log.error("清理 Redis 失败：{}", e.getMessage()))
//                .subscribe();
//    }
//}
//
