package com.guanshiyun.reactiveredis;

import com.guanshiyun.reactiveredis.listenerConfig.ListenerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedisReactiveAutoConfiguration {

    @Bean
    public ReactiveRedisConfig  reactiveRedisConfig(){
        return new ReactiveRedisConfig();
    }

    @Bean
    public ListenerConfig listenerConfig(){
        return new ListenerConfig();
    }

}
