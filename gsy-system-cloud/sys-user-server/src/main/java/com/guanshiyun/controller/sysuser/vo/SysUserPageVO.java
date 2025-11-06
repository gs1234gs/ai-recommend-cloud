package com.guanshiyun.controller.sysuser.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserPageVO {

    //用户名
    private String username;
    //昵称
    private String nickname;
}
