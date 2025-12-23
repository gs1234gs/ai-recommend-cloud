package com.guanshiyun.aienums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AddressApiUrlEnum {


    ADDRESS_FIND_BY_USER_ID("根据用户id获取地址", "/address/findByUserId/{userId}"),
    ADDRESS_FIND_BY_ID("根据id获取地址", "/address/findById/{id}"),
    ADDRESS_FIND_BY_ORDER_ID("根据订单id获取地址", "/address/findByOrderId/{orderId}"),
    ADDRESS_FIND_BY_ORDER_IDS("根据订单id获取地址", "/address/findByOrderIds/{orderIds}"),
    ;
    private final String name;
    private final String url;


}
