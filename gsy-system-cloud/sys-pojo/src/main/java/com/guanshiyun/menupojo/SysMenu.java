package com.guanshiyun.menupojo;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单实体类
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table("sys_menu")
public class SysMenu extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //菜单id
   @Id
   private Long id;
   //菜单名称
    private String name;
    //父级菜单id
    private Long parentId;
    //显示顺序
    private int sort;
    //路由地址
    private String path;
    //组件路径
    private String component;
    //路由参数
    private String query;
    //路由名称
    private String routeName;
    //是否外链,0是，，1否
    private short isFrame;
    //是否缓存，0是，1否
    private short isCache;
    //菜单类型，M目录，C菜单,F按钮
    private String type;
    //菜单状态，0显示，1隐藏
    private short visible;
    //菜单状态，0正常，1停用
    private short status;
    //权限标识
    private String perms;
    //图标
    private String icon;
    //备注
    private String remark;
}
