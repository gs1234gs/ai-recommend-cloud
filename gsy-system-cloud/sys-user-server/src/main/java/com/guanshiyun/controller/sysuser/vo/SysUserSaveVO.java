package com.guanshiyun.controller.sysuser.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode()
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysUserSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    //部门id
    private int deptId;
    //用户名
    private String username;
    //姓名
    private String nickName;
    //用户类型
    private short type;
    //用户邮箱
    private String email;
    //手机号码
    private String phoneNumber;
    //身份证
    private String idCard;
    //性别，0男，1女，2未知
    private short gender;
    //用户头像
    private String image;
    //用户密码
    private String password;

    //账号状态（0正常 1停用）
    private short status;
    //登陆时间
    private LocalDateTime loginTime;
    //备注
    private String remark;
    //角色id
    private List<Long> roleIdList;
    //租户id
    private Long tenantId;
}
