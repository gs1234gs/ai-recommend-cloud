package com.guanshiyun.behaviorenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BehaviorClickApiUrlEnum {
    CLICK_FIND_BY_ROWS("获取点击记录","/click/findByRows"),
    ;
    private final String name;
    private final String value;
}
