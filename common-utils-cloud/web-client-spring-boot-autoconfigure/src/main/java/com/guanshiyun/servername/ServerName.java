package com.guanshiyun.servername;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ServerName {
    //ai
    AI_APP("ai服务", "http://ai-app/ai-api"),
    GOODS_APP("商品服务", "http://goods-app/goods-api"),
    BEHAVIOR_APP("行为服务", "http://behavior-app/behavior-api"),
    SYSTEM_APP("系统服务", "http://system-app/sys-api"),
    ORDER_APP("订单服务", "http://order-app/order-api"),
    ;
    private final String name;
    private final String value;
    public static final String[] ARRAYS = Arrays.stream(values())
            .map(e -> e.name)
            .toArray(String[]::new);
}
