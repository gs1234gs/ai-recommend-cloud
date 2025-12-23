package com.guanshiyun.gorseenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum GorseFeedbackEnum {
    CLICK("点击","click"),
    COLLECT("收藏","collect"),
    BROWSE("浏览","browse"),
    PURCHASE("购买","purchase"),
    SEARCH("搜索","search");
    private final String name;
    private final String value;
    public static final String[] ARRAYS = Arrays.stream(values())
            .map(e -> e.name)
            .toArray(String[]::new);
}
