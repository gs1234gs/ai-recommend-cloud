package com.guanshiyun.utils;

import java.lang.annotation.*;

// 文件：DisableBusinessWebFilter.java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableBusinessWebFilter {
    // 可以留空，作为标记注解
}
