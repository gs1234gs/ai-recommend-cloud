package com.guanshiyun.behaviorenums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GuestEnum {
    GUEST_USER_ID("游客id","guest");
    private final String name;
    private final String value;
}
