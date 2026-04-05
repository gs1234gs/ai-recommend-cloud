package com.guanshiyun.controller.userrole.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserRoleVO {
    //用户id
    private Long userId;
    //角色id
    private List<Long> roleId;
}
