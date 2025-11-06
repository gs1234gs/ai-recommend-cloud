package com.guanshiyun.controller.sysuser.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class SysUserPageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //用户名
    private String username;
    //昵称
    private String nickName;
}
