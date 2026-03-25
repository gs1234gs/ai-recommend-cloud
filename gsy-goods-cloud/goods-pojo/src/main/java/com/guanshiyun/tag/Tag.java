package com.guanshiyun.tag;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

import java.time.LocalDateTime;


/**
 * 商品标签
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table("tag")
@Accessors(chain = true)
public class Tag extends BasePojo {
    /** 标签主键ID */
    @Id
    private Long id;

    /** 标签名称 */
    private String name;

    /** 标签编码（英文或拼音，方便系统使用） */
    private String code;

    /** 标签颜色（用于前端展示，例如 #FF9900） */
    private String color;

    /** 状态,是否禁用（0=启用，1=禁用） */
    private short status;

    /** 标签权重（用于推荐优先级或排序） */
    private BigDecimal weight;
    /** 排序值 */
    private Integer sort;
    //材料组成
    private String composition;
    //产地
    private String placeOfOrigin;
    //重量
    private BigDecimal productWeight;
    //生产日期
    private LocalDateTime productionDate;
    //保质期，单位：天
    private Integer shelfLife;
    /** 标签描述 */
    private String description;
}
