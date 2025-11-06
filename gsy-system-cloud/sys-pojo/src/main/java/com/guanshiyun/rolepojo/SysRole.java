package com.guanshiyun.rolepojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;
/**
 * 角色实体
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_role")
public class SysRole {
    // 角色id
    @Id
    private BigInteger id;
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
    // 删除标志（1：删除，0：未删除）
    private short delFlag;
    // 创建者id
    private BigInteger creatorId;
    // 创建时间
    private LocalDateTime createTime;
    // 更新者id
    private BigInteger updaterId;
    // 更新时间
    private LocalDateTime updateTime;
    // 备注
    private String remark;
}
