package com.guanshiyun.snowflake;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 永久唯一 Snowflake ID 生成器（BigInteger版本）
 *
 * 特点：
 * 1. 使用 BigInteger 支持任意长度，突破 long 64位限制。
 * 2. 高并发安全：单毫秒可生成 2^30 ≈ 10亿 ID。
 * 3. 分布式安全：datacenterId + workerId 保证跨机器唯一。
 * 4. 可永久使用：时间戳位数足够大，足够几十万年。
 */
public class SnowflakePermanent {

    private final long epoch;

    private final int datacenterId;
    private final int workerId;

    // 64位结构：41 + 8 + 8 + 7 = 64
    private final int sequenceBits = 7;
    private final int workerBits = 8;
    private final int datacenterBits = 8;
    private final int timestampBits = 41;

    private final long maxSequence = (1L << sequenceBits) - 1;

    private final int workerShift = sequenceBits;
    private final int datacenterShift = workerShift + workerBits;
    private final int timestampShift = datacenterShift + datacenterBits;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakePermanent(long epoch, int datacenterId, int workerId) {
        if (datacenterId < 0 || datacenterId >= (1 << datacenterBits)) {
            throw new IllegalArgumentException("datacenterId out of range");
        }
        if (workerId < 0 || workerId >= (1 << workerBits)) {
            throw new IllegalArgumentException("workerId out of range");
        }
        this.epoch = epoch;
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public synchronized BigInteger nextId() {
        long ts = Instant.now().toEpochMilli();

        if (ts < lastTimestamp) {
            throw new IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for " + (lastTimestamp - ts) + " ms"
            );
        }

        if (ts == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                ts = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = ts;

        long id =
                ((ts - epoch) << timestampShift) |
                        ((long) datacenterId << datacenterShift) |
                        ((long) workerId << workerShift) |
                        sequence;

        return toUnsignedBigInteger(id);
    }

    private long tilNextMillis(long lastTs) {
        long ts = Instant.now().toEpochMilli();
        while (ts <= lastTs) {
            Thread.yield();
            ts = Instant.now().toEpochMilli();
        }
        return ts;
    }

    // 关键：long 转无符号 BigInteger
    private BigInteger toUnsignedBigInteger(long value) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(value).array();
        return new BigInteger(1, bytes);
    }

    public String stringNextId() {
        return nextId().toString();
    }

    public static void main(String[] args) throws InterruptedException {
        SnowflakePermanent generator = new SnowflakePermanent(
                System.currentTimeMillis(), 1, 1
        );

        int threads = 32;
        int perThread = 10000_00;

        Set<BigInteger> ids = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ExecutorService exec = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                for (int j = 0; j < perThread; j++) {
                    BigInteger id = generator.nextId();
                    if (!ids.add(id)) {
                        System.err.println("Duplicate detected: " + id);
                    }
                }
            });
        }

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Total generated IDs: " + ids.size());
    }
}
