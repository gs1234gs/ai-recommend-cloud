package com.guanshiyun.aienums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderApiUrlEnum {


    ORDER_FIND_BY_ROWS("获取指定条数订单","/order/findByRows"),
    ORDER_FIND_BY_ID("根据id获取订单","/order/findById"),
    ORDER_FIND_BY_USER_ID("根据用户id获取订单","/order/findByUserId"),
    ;
    private final   String  name;
    private final String url;

}
