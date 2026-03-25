package com.guanshiyun.mylong;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public class MyLong {

    public Long myLong(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.getLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常，number：{}", number);
            throw new RuntimeException("转换Long异常", e);
        }
        return mylong;
    }
    //重载，允许返回null
    public Long LongOrNull(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.parseLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常", e);
        }
        return mylong;
    }
}
