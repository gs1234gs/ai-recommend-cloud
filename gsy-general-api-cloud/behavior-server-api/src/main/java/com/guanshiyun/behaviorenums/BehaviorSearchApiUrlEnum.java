package com.guanshiyun.behaviorenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BehaviorSearchApiUrlEnum {
    BEHAVIOR_SAVE("保存搜索记录","/search/save"),
    BEHAVIOR_FIND_BY_ROWS("获取搜索记录","/search/findByRows"),
    ;
    private final String name;
    private final String value;
}
