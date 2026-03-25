package com.guanshiyun.relationpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table("sys_role_menu")
public class SysRoleMenu {
    //主键id
    @Id
    private Long id;
    //角色id
    private Long roleId;
    //菜单id
    private Long menuId;
}
