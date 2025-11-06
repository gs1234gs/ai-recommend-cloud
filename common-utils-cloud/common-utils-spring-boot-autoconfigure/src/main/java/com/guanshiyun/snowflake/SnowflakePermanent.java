package com.guanshiyun.snowflake;

import java.math.BigInteger;
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

    /** 自定义纪元（毫秒），ID 的时间戳从这里开始计算 */
    private final long epoch;

    /** 数据中心ID（0~255），分布式环境唯一 */
    private final int datacenterId;

    /** 机器ID（0~255），分布式环境唯一 */
    private final int workerId;

    /** 序列号位数（每毫秒生成量） */
    private final int sequenceBits = 30;

    /** 机器ID位数 */
    private final int workerBits = 8;

    /** 数据中心位数 */
    private final int datacenterBits = 8;

    /** 时间戳位数（50位，可表示数万年毫秒） */
    private final int timestampBits = 50;

    /** 单毫秒最大序列号 */
    private final long maxSequence = (1L << sequenceBits) - 1;

    /** 机器ID左移位数 */
    private final int workerShift = sequenceBits;

    /** 数据中心ID左移位数 */
    private final int datacenterShift = workerShift + workerBits;

    /** 时间戳左移位数 */
    private final int timestampShift = datacenterShift + datacenterBits;

    /** 上一次生成ID的时间戳 */
    private long lastTimestamp = -1L;

    /** 当前毫秒内序列号 */
    private long sequence = 0L;

    /**
     * 构造器
     *
     * @param epoch        自定义纪元毫秒
     * @param datacenterId 数据中心ID
     * @param workerId     机器ID
     */
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

    /**
     * 生成下一个唯一ID
     *
     * synchronized 保证高并发线程安全
     *
     * @return 唯一ID（BigInteger）
     */
    public synchronized BigInteger nextId() {
        long ts = Instant.now().toEpochMilli(); // 当前毫秒时间戳

        // 时钟回拨保护
        if (ts < lastTimestamp) {
            throw new IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for " + (lastTimestamp - ts) + " ms"
            );
        }

        if (ts == lastTimestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // 序列号耗尽，等待下一个毫秒
                ts = tilNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = ts;

        // 组合时间戳、数据中心、机器ID、序列号生成 BigInteger ID
        BigInteger tsPart = BigInteger.valueOf(ts - epoch).shiftLeft(timestampShift);
        BigInteger dcPart = BigInteger.valueOf(datacenterId).shiftLeft(datacenterShift);
        BigInteger wkPart = BigInteger.valueOf(workerId).shiftLeft(workerShift);
        BigInteger seqPart = BigInteger.valueOf(sequence);

        return tsPart.or(dcPart).or(wkPart).or(seqPart);
    }

    /**
     * 阻塞等待到下一个毫秒，保证序列号唯一
     *
     * @param lastTs 上一次生成ID的时间戳
     * @return 当前毫秒时间戳
     */
    private long tilNextMillis(long lastTs) {
        long ts = Instant.now().toEpochMilli();
        while (ts <= lastTs) {
            Thread.yield(); // CPU友好等待
            ts = Instant.now().toEpochMilli();
        }
        return ts;
    }
    public  String StringNextId(){
        return nextId().toString();
    }

    /**
     * 主方法：测试高并发生成ID
     */
    public static void main(String[] args) throws InterruptedException {  // Snowflake ID 生成器
        SnowflakePermanent generator = new SnowflakePermanent(
                System.currentTimeMillis(), 1, 1
        );

        int threads = 32;      // 线程数
        int perThread = 10000_00;  // 每线程生成 ID 数量

        // 使用 ConcurrentHashMap 来做线程安全的 Set
        Set<BigInteger> ids = Collections.newSetFromMap(new ConcurrentHashMap<>());

        // 创建线程池
        ExecutorService exec = Executors.newFixedThreadPool(threads);

        // 并发生成 ID
        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                for (int j = 0; j < perThread; j++) {
                    BigInteger id = generator.nextId();

                    // 查重逻辑：如果已经存在，打印重复 ID
                    if (!ids.add(id)) {
                        System.err.println("Duplicate detected: " + id);
                        System.out.println("id重复了: "+id);
                    }
                }
            });
        }

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);

        // 输出总共生成的唯一 ID 数量
        System.out.println("Total generated IDs: " + ids.size());

        // 如果想打印前 10 个 ID 示例
//        ids.stream().limit(10).forEach(id -> System.out.println("Sample ID: " + id));
    }
}
