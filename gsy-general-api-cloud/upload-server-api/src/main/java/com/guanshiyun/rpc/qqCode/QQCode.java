package com.guanshiyun.rpc.qqCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QQCode {
    //目标邮箱
    private String email;
    //验证码
    private String code;
    //有效时长
    private int expire;
}
