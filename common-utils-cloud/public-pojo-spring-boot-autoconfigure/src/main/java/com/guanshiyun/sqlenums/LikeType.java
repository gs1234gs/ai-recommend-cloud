package com.guanshiyun.sqlenums;

public enum LikeType {
    /**
     * LIKE '%value%'
     */
    CONTAINS,
    /**
     * LIKE 'value%'
     */
    STARTS_WITH,
    /**
     * LIKE '%value'
     */
    ENDS_WITH,
    /**
     * LIKE 'value'（精确）
     */
    EXACT
}
