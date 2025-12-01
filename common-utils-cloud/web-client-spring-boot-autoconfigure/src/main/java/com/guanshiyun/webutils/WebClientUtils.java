package com.guanshiyun.webutils;

import org.springframework.core.ParameterizedTypeReference;

import java.math.BigInteger;

public class WebClientUtils {
    public static <T> ParameterizedTypeReference<T> typeRef() {
        return new ParameterizedTypeReference<T>() {};
    }
    public static BigInteger bigInteger(Object number) {
        BigInteger bigInteger = null;;
        try {
            bigInteger = new BigInteger(number.toString().trim());
        } catch (Exception e) {
            throw new RuntimeException("转换BigInteger异常", e);
        }
        return bigInteger;
    }
}
