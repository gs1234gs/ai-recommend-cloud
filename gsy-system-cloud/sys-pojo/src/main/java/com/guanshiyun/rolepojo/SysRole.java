package com.guanshiyun.rolepojo;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
/**
 * 角色实体
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_role")
public class SysRole extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // 角色id
    @Id
    private Long id;
    // 角色名称
    private String name;
    // 角色角色权限符
    private String roleKey;
    // 角色排序
    private int sort;
    //数据范围（1：所有数据权限，2：自定义数据权限，3：本部门数据权限，4：本部门及以下数据权限，5：仅本人数据权限）
    private short dataScope;
    // 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
    private short menuCheckStrictly;
    // 部门树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
    private short deptCheckStrictly;
    // 角色状态（1：停用，0：启用）
    private short status;
    // 备注
    private String remark;
}
