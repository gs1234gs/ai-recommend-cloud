package com.guanshiyun.webutils;


import com.alibaba.fastjson2.TypeReference;
import org.springframework.core.ParameterizedTypeReference;



public class WebClientUtils {
    public static <T> ParameterizedTypeReference<T> typeRef() {
        return new ParameterizedTypeReference<T>() {};
    }
    public static Long Long(Object number) {
        Long Long = null;;
        try {
            Long = new Long(number.toString().trim());
        } catch (Exception e) {
            throw new RuntimeException("转换Long异常", e);
        }
        return Long;
    }
    public static <T> TypeReference<T> typeRefFastJson2() {
        return new TypeReference<>() {};
    }
}
