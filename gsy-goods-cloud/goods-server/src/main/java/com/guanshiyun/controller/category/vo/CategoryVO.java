package com.guanshiyun.controller.category.vo;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@ToString
public class CategoryVO {
    /** 分类主键ID */
    private BigInteger id;

    /** 分类名称 */
    private String name;

    /** 分类层级（如 1=一级分类，2=二级分类） */
    private Integer level;

    /** 分类编码（用于快速检索或编码管理） */
    private String code;

    /** 分类描述 */
    private String description;

    /** 排序字段（越小越靠前） */
    private Integer sortOrder;

    /** 分类图标（URL） */
    private String iconUrl;

    /** 状态,是否启用（1=启用，0=禁用） */
    private short status;

    /** 是否为推荐分类（如首页展示）,(0 = 否，1表示是) */
    private short recommended;

}
