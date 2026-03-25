package com.guanshiyun.util;


import java.util.UUID;
/**
 * 数字转 UUID 工具类 (Long 版本)
 *
 * 逻辑说明：
 * 1. 输入是一个 64 位的 long。
 * 2. UUID 需要 128 位 (两个 long)。
 * 3. 策略：将输入的 long 作为 UUID 的 "leastSignificantBits" (低位)，
 *    "mostSignificantBits" (高位) 固定为 0。
 *    这样生成的 UUID 格式为：00000000-0000-0000-xxxx-xxxxxxxxxxxx
 */
public class NumberToUUIDUtils {

    /**
     * 将 Long 转换为 UUID
     *
     * @param value 输入的 Long 值 (如果是 null，返回 null 或抛异常，视业务需求而定)
     * @return 对应的 UUID
     */
    public static UUID toUUID(Long value) {
        if (value == null) {
            return null; // 或者 throw new IllegalArgumentException("Value cannot be null");
        }

        // 高位补 0，低位直接使用 long 值
        // UUID 构造函数参数：(mostSignificantBits, leastSignificantBits)
        return new UUID(0L, value);
    }

    /**
     * 反向操作：从 UUID 还原回 Long
     * (仅当 UUID 是由本工具类生成，或确认高位为 0 时有效)
     */
    public static Long fromUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return uuid.getLeastSignificantBits();
    }

}
