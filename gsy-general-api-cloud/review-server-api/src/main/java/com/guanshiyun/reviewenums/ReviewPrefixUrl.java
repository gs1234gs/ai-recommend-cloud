package com.guanshiyun.reviewenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewPrefixUrl {
    PREFIX_URL("PREFIX_URL","/review-api");
    private final String name;
    private final String value;
}
