package com.guanshiyun.relationpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_role_menu")
public class SysRoleMenu {
    //主键id
    @Id
    private BigInteger id;
    //角色id
    private BigInteger roleId;
    //菜单id
    private BigInteger menuId;
}
