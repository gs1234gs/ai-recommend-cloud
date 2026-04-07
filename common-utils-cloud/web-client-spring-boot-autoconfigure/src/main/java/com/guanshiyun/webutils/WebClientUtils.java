package com.guanshiyun.webutils;


import com.alibaba.fastjson2.TypeReference;
import org.springframework.core.ParameterizedTypeReference;



public class WebClientUtils {
    public static <T> ParameterizedTypeReference<T> typeRef() {
        return new ParameterizedTypeReference<T>() {};
    }
    public static Long webLong(Object number) {
        Long longNum = null;;
        try {
            longNum = Long.valueOf(number.toString().trim());
        } catch (Exception e) {
            throw new RuntimeException("转换Long异常", e);
        }
        return longNum;
    }
    public static <T> TypeReference<T> typeRefFastJson2() {
        return new TypeReference<>() {};
    }
}
