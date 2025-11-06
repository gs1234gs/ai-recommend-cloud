package com.guanshiyun.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum SortOrderEnum {
    ASC("ASC", "升序"),
    DESC("DESC", "降序");

    private final String key;
    private final String value;

    SortOrderEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }
    @JsonCreator
    public static SortOrderEnum fromKey(String key) {
        if (key == null) return null;
        for (SortOrderEnum order : values()) {
            if (order.key.equalsIgnoreCase(key.trim())) {
                return order;
            }
        }
        throw new IllegalArgumentException("未知排序方向: " + key + "，支持: ASC, DESC");
    }

    //保留这些判断方法，供业务使用
    public boolean isAsc() {
        return this == ASC;
    }

    public boolean isDesc() {
        return this == DESC;
    }

    // 如果需要，返回字符串（供外部构建查询）
    public String direction() {
        return this.key; // 返回 "ASC" 或 "DESC"
    }

    @Override
    public String toString() {
        return key;
    }
}
