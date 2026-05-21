package com.guanshiyun.uploadUrlEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QQEnumUrlApi {
    //请求发送验证码路径
    QQ_EMAIL_CODE_SEND("发送验证码","/qqEmailCode/sendQQEmailCode");

    private final String name;
    private final String value;
}
