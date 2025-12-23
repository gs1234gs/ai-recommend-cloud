package com.guanshiyun.behaviorenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BehaviorColleckApiUrlEnum {

    COLLECT_FIND_BY_ROWS("获取收藏记录","/collect/findByRows"),
    ;
    private final String name;
    private final String value;
}
