package com.guanshiyun.snowflake;


import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 永久唯一 Snowflake ID 生成器（Long版本）
 *
 */
public class SnowflakePermanent {

    // 起始时间戳 (可选，通常设为某个特定日期，这里动态设为当前时间演示，生产环境建议固定)
    private final long epoch;

    private final int datacenterId;
    private final int workerId;

    // 位数定义
    private final int sequenceBits = 7;
    private final int workerBits = 8;
    private final int datacenterBits = 8;
    private final int timestampBits = 41;

    // 最大值
    private final long maxSequence = (-1L ^ (-1L << sequenceBits)); // 等价于 (1L << 7) - 1
    private final long maxWorkerId = (-1L ^ (-1L << workerBits));
    private final long maxDatacenterId = (-1L ^ (-1L << datacenterBits));

    // 位移量
    private final int workerShift = sequenceBits;
    private final int datacenterShift = sequenceBits + workerBits;
    private final int timestampShift = sequenceBits + workerBits + datacenterBits;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakePermanent(long epoch, int datacenterId, int workerId) {
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException("datacenterId out of range: " + datacenterId);
        }
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("workerId out of range: " + workerId);
        }
        this.epoch = epoch;
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个 ID
     * 返回类型为 long (基本类型)，自动装箱为 Long 如果需要
     */
    public synchronized long nextId() {
        long ts = Instant.now().toEpochMilli();

        // 时钟回拨处理
        if (ts < lastTimestamp) {
            throw new IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for " + (lastTimestamp - ts) + " ms"
            );
        }

        if (ts == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // 当前毫秒内序列号用完，等待下一毫秒
                ts = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = ts;

        // 41 位大约能使用 69 年 (2^41 ms)
        long id = ((ts - epoch) << timestampShift) |
                ((long) datacenterId << datacenterShift) |
                ((long) workerId << workerShift) |
                sequence;

        return id;
    }

    private long tilNextMillis(long lastTs) {
        long ts = Instant.now().toEpochMilli();
        while (ts <= lastTs) {
            ts = Instant.now().toEpochMilli();
            // 防止死循环过久占用 CPU，可酌情添加 Thread.yield() 或短暂休眠，但通常不需要
            // Thread.yield();
        }
        return ts;
    }

    /**
     * 生成字符串格式的 ID (方便前端传输或日志打印)
     */
    public String stringNextId() {
        return String.valueOf(nextId());
    }

    public static void main(String[] args) throws InterruptedException {
        // 使用固定纪元时间 (例如 2024-01-01)，避免每次运行程序纪元不同导致 ID 变化规律不同
        // 生产环境请配置为一个固定的过去时间戳
        long customEpoch = Instant.parse("2024-01-01T00:00:00Z").toEpochMilli();

        SnowflakePermanent generator = new SnowflakePermanent(customEpoch, 1, 1);

        int threads = 32;
        int perThread = 100_000; // 调整数量以便快速测试

        // 使用 Long 作为 Key
        Set<Long> ids = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ExecutorService exec = Executors.newFixedThreadPool(threads);

        System.out.println("开始生成 ID...");
        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                for (int j = 0; j < perThread; j++) {
                    long id = generator.nextId(); // 接收基本类型 long
                    // 自动装箱为 Long 放入 Set
                    if (!ids.add(id)) {
                        System.err.println("Duplicate detected: " + id);
                    }
                }
            });
        }

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("Total generated IDs: " + ids.size());
        System.out.println("Expected: " + (threads * perThread));
        System.out.println("Time taken: " + (end - start) + " ms");
        System.out.println("Success: " + (ids.size() == threads * perThread));
    }
}
