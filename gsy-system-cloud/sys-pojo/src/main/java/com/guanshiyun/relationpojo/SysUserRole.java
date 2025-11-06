package com.guanshiyun.relationpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Builder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_user_role")
public class SysUserRole {
    //主键id
    @Id
    private BigInteger id;
    //用户Id
    private BigInteger userId;
    //角色Id
    private BigInteger roleId;
}
