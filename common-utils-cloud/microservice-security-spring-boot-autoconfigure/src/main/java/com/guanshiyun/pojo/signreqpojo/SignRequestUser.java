package com.guanshiyun.pojo.signreqpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignRequestUser {
    // 用户名
    private String username;
    // 密码
    private String password;
}
