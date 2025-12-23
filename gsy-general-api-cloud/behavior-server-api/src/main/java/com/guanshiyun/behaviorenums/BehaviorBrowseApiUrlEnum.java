package com.guanshiyun.behaviorenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BehaviorBrowseApiUrlEnum {
   BROWSE_FIND_BY_ROWS("获取浏览记录","/browse/findByRows"),
    ;
    private final String name;
    private final String value;
}
