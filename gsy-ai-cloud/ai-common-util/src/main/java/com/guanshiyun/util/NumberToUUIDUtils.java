package com.guanshiyun.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class NumberToUUIDUtils {
    public static UUID toUUID(BigInteger bigInt) {
        byte[] bytes = bigInt.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(16); // UUID 固定 16 字节
        // 如果 bytes 长度超过 16，则只取低位；不足则高位补 0
        if (bytes.length > 16) {
            buffer.put(bytes, bytes.length - 16, 16);
        } else {
            buffer.position(16 - bytes.length);
            buffer.put(bytes);
        }
        buffer.flip();
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }

}
