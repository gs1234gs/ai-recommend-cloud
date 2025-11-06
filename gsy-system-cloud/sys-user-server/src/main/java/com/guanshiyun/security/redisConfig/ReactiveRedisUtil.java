package com.guanshiyun.security.redisConfig;


import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ReactiveRedisUtil {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public ReactiveRedisUtil(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    /**
     * 设置值
     */
    public Mono<Boolean> set(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并指定过期时间
     */
    public Mono<Boolean> setWithExpire(String key, Object value, long timeoutSeconds) {
        return reactiveRedisTemplate.opsForValue()
                .set(key, value)
                .flatMap(result -> reactiveRedisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds)));
    }

    /**
     * 获取值
     */
    public Mono<Object> get(String key) {
        return reactiveRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     */
    public Mono<Boolean> hDel(String key) {
        return reactiveRedisTemplate.opsForHash()
                .remove(key)
                .map(count -> count > 0); // 转成 Boolean
    }


    /**
     * 设置过期时间
     */
    public Mono<Boolean> expire(String key, long timeoutSeconds) {
        timeoutSeconds  *= 60;
        return reactiveRedisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 检查键是否存在
     */
    public Mono<Boolean> hasKey(String key) {
        return reactiveRedisTemplate.hasKey(key);
    }

    /**
     * 哈希表设置字段值
     */
    public Mono<Boolean> hSet(Object key, Object field, Object value) {
        return reactiveRedisTemplate.opsForHash().put(convertToString(key),
                convertToString( field), value);
    }
    /**
     * 哈希表获取字段值
     */
    public Mono<Object> hGet(Object key, Object field) {
        return reactiveRedisTemplate.opsForHash().get(convertToString(key),
                convertToString( field));
    }

    /**
     * 哈希表删除字段
     */
    public Mono<Long> hDel(String key, String field) {
        return reactiveRedisTemplate.opsForHash().remove(key, field);
    }

    /**
     * 列表左推入元素
     */
    public Mono<Long> lPush(String key, Object value) {
        return reactiveRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 列表右弹出元素
     */
    public Mono<Object> rPop(String key) {
        return reactiveRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 自增
     */
    public Mono<Long> increment(String key, long delta) {
        return reactiveRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减
     */
    public Mono<Long> decrement(String key, long delta) {
        return reactiveRedisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 获取所有哈希字段
     */
    public Flux<Object> hKeys(String key) {
        return reactiveRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表所有值
     */
    public Flux<Object> hValues(String key) {
        return reactiveRedisTemplate.opsForHash().values(key);
    }

    /**
     * 获取列表长度
     */
    public Mono<Long> lLen(String key) {
        return reactiveRedisTemplate.opsForList().size(key);
    }

    // 自定义转换方法
    private String convertToString(Object obj) {
        // 如果是其他对象类型，使用 toString() 转换
        return obj.toString();
    }
}