package com.guanshiyun.biginteger;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class MyBigInteger {

    public BigInteger bigInteger(Object number) {
        BigInteger bigInteger = null;;
        try {
            bigInteger = new BigInteger(number.toString().trim());
        } catch (Exception e) {
            throw new RuntimeException("转换BigInteger异常", e);
        }
        return bigInteger;
    }
    //重载，允许返回null
    public BigInteger bigIntegerOrNull(Object number) {
        BigInteger bigInteger = null;;
        try {
            bigInteger = new BigInteger(number.toString().trim());
        } catch (Exception e) {
            log.error("转换BigInteger异常", e);
        }
        return bigInteger;
    }
}
