package com.guanshiyun.webutils;

import org.springframework.core.ParameterizedTypeReference;

public class WebClientUtils {
    public static <T> ParameterizedTypeReference<T> typeRef() {
        return new ParameterizedTypeReference<T>() {};
    }
}
