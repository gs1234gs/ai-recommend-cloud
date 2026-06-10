package com.guanshiyun.redisconfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.reactiveredis.ReactiveRedisConfig;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.reactiveredis.listenerConfig.ListenerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;



@Configuration
public class RedisReactiveAutoConfiguration {

    @Bean
    public ReactiveRedisConfig reactiveRedisConfig(){
        return new ReactiveRedisConfig();
    }

    @Bean
    public ListenerConfig listenerConfig(){
        return new ListenerConfig();
    }

    @Bean
    public ReactiveRedisUtil reactiveRedisUtil(ReactiveRedisTemplate<String, String> reactiveRedisTemplate, ObjectMapper objectMapper) {
        return new ReactiveRedisUtil(reactiveRedisTemplate,objectMapper);
    }

}
