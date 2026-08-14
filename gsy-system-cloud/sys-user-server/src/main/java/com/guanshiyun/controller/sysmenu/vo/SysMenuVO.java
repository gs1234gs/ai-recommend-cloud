package com.guanshiyun.controller.sysmenu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class SysMenuVO
{
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
    //创建者id
    private Long creatorId;
    //创建时间
    private LocalDateTime createTime;
    //更新者id
    private Long updaterId;
    //更新时间
    private LocalDateTime updateTime;
    //备注
    private String remark;
    // 子菜单列表,忽略，这个不是数据库的字段
    // 子菜单列表
    private List<SysMenuVO> children;
}
