package com.guanshiyun.reactiveredis.listenerConfig;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class ListenerConfig {
    @Bean
    public ApplicationListener<ApplicationReadyEvent> redisConnectionCheckListener(ReactiveRedisConnectionFactory factory) {
        return event -> {
            log.info("应用已启动，检查 Redis 连接...");

            // 直接使用 getReactiveConnection().ping() —— 这是新的推荐方式
            Mono<String> pingMono = factory.getReactiveConnection().ping();

            pingMono.subscribe(
                    pong -> log.info("Redis 连接成功，返回：{}", pong),
                    error -> log.error("Redis 连接失败：{}", error.getMessage())
            );
        };
    }

}
