package com.guanshiyun.reactiveredis;


import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReactiveRedisUtil {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    /** 设置值 */
    public Mono<Boolean> set(String key, String value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    /** 设置值并指定过期时间 */
    public Mono<Void> setWithExpire(String key, Object valueObject, long timeoutSeconds) {
        String value = JSONObject.toJSONString(valueObject);
        return redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds))
                .then();
    }

    /** 获取值（JSON 转换） */
    public <T> Mono<T> get(String key, Class<T> clazz) {
        return redisTemplate.opsForValue()
                .get(key)
                .map(json -> JSONObject.parseObject(json, clazz));
    }

    /** 获取原始字符串值 */
    public Mono<String> get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除键 */
    public Mono<Boolean> del(String key) {
        return redisTemplate.delete(key).map(count -> count > 0);
    }

    /** 设置过期时间 */
    public Mono<Boolean> expire(String key, long timeoutSeconds) {
        return redisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
    }

    /** 检查键是否存在 */
    public Mono<Boolean> hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /** 哈希表设置字段值 */
    public Mono<Boolean> hSet(String key, String field, String value) {
        return redisTemplate.opsForHash().put(key, field, value);
    }

    /** 哈希表获取字段值 */
    public Mono<String> hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field)
                .map(obj -> obj != null ? obj.toString() : null);
    }

    /** 哈希表删除字段 */
    public Mono<Boolean> hDelField(String key, String field) {
        return redisTemplate.opsForHash().remove(key, field).map(count -> count > 0);
    }

    /** 列表左推入元素 */
    public Mono<Long> lPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /** 列表右弹出元素 */
    public Mono<String> rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /** 自增 */
    public Mono<Long> increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /** 自减 */
    public Mono<Long> decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /** 获取所有哈希字段 */
    public Mono<Set<Object>> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key)
                .collectList()
                .map(list -> Set.copyOf(list));
    }

    /** 获取哈希表所有值 */
    public Mono<List<Object>> hValues(String key) {
        return redisTemplate.opsForHash().values(key)
                .collectList();
    }

    /** 获取列表长度 */
    public Mono<Long> lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }


    /**
     * 获取列表指定范围的元素 (用于分页下拉)
     * @param key Redis Key
     * @param start 起始索引 (包含)
     * @param end 结束索引 (包含)
     * @return 元素列表
     */
    public Mono<List<String>> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end).collectList();
    }

    /**
     * 批量向右推送列表元素 (用于初始化/更新候选池)
     * @param key Redis Key
     * @param values 元素集合
     * @return 操作完成信号
     */
    public Mono<Void> rPushAll(String key, List<String> values) {
        if (values == null || values.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(values)
                .flatMap(val -> redisTemplate.opsForList().rightPush(key, val))
                .then();
    }

    /**
     * 删除旧列表并重新写入 (原子性较差，但在推荐场景可接受，或者使用 Lua 脚本优化)
     * 这里采用先删后写的方式重置候选池
     */
    public Mono<Void> refreshList(String key, List<String> values) {
        return redisTemplate.delete(key)
                .thenMany(Flux.fromIterable(values))
                .flatMap(val -> redisTemplate.opsForList().rightPush(key, val))
                .then();
    }
}
