package com.guanshiyun.security.redisConfig;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Base64;

@Component
public class ReactiveRedisFileStorageUtil {
    private final ReactiveRedisTemplate<String, byte[]> template;
    private static final String FILE_KEY_PREFIX = "file:";

    public ReactiveRedisFileStorageUtil(ReactiveRedisTemplate<String, byte[]> template) {
        this.template = template;
    }

    /**
     * 存储文件的二进制流到 Redis
     *
     * @param fileName 文件名（用作 Redis key 后缀）
     * @param dataBufferFlux 文件流内容
     * @return Mono<Void> 操作完成的信号
     */
    public Mono<Void> storeFile(String fileName, Flux<DataBuffer> dataBufferFlux) {
        String key = FILE_KEY_PREFIX + fileName;

        return DataBufferUtils.join(dataBufferFlux)
                // join 将多个 DataBuffer 合并成一个大缓冲区
                .flatMap(dataBuffer -> {
                    // 读取所有内容到 byte[]
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // 释放内存，防止泄露
                    DataBufferUtils.release(dataBuffer);
                    // 写入 Redis，返回 Mono<Boolean>
                    return template.opsForValue()
                            .set(key, bytes);
                })
                // 丢弃 Boolean 值，只关心完成事件
                .then();
    }

    /**
     * 从 Redis 获取文件的二进制流
     *
     * @param fileName 文件名
     * @return Mono<Flux<DataBuffer>> 返回响应式的 DataBuffer 流供下载
     */
    public Mono<Flux<DataBuffer>> retrieveFile(String fileName) {
        String key = "file:" + fileName;

        return template.opsForValue()
                .get(key)
                .switchIfEmpty(Mono.error(new RuntimeException("File not found")))
                .map(bytes -> {
                    // 将 byte[] 转换为 DataBuffer
                    DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
                    return Flux.just(dataBuffer);
                });
    }


    /**
     * 以 Base64 编码存储文件内容并返回编码字符串
     */
    public Mono<String> storeFileAndReturnBase64(String fileName, Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux)
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return template.opsForValue().set(FILE_KEY_PREFIX + fileName, bytes)
                            .thenReturn(bytes);
                })
                .map(Base64.getEncoder()::encodeToString);
    }

    /**
     * 获取 Base64 编码的文件内容
     */
    public Mono<String> retrieveFileAsBase64(String fileName) {
        return retrieveFile(fileName)
                .flatMapMany(flux -> flux)
                .single()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Base64.getEncoder().encodeToString(bytes);
                });
    }


    /**
     * 删除 Redis 中存储的文件
     *
     * @param fileName 文件名（用作 Redis key 后缀）
     * @return Mono<Void> 操作完成的信号
     */
    public Mono<Void> deleteFile(String fileName) {
        String key = FILE_KEY_PREFIX + fileName;
        return template.opsForValue()
                .delete(key) // 返回 Mono<Boolean>
                .flatMap(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException("File not found"));
                    }
                });
    }

    /**
     * 设置过期时间
     * */
    public Mono<Boolean> expire(String key, Duration ttl) {
        return template.expire(key, ttl);
    }
}
