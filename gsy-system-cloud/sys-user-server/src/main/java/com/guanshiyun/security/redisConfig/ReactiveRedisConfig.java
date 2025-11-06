package com.guanshiyun.security.redisConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class ReactiveRedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper objectMapper // 注入你配置好的 ObjectMapper
    ) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper); // ✅ 使用你自定义的 ObjectMapper

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }




    @Bean
    public ReactiveRedisTemplate<String, byte[]> reactiveByteRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, byte[]> ctx =
                RedisSerializationContext.<String, byte[]>newSerializationContext(StringRedisSerializer.UTF_8)
                        .value(RedisSerializationContext.SerializationPair.byteArray())
                        .build();
        return new ReactiveRedisTemplate<>(factory, ctx);
    }
}