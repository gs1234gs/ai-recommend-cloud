package com.guanshiyun.zmoudle.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* 用户实体
* */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {

    //用户id
    private Long id;
    //用户名
    private String username;
    //姓名
    private String nickName;
}
