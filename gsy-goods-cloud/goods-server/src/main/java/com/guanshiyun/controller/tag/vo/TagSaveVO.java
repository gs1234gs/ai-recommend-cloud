package com.guanshiyun.controller.tag.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagSaveVO {
    private BigInteger id;
    /** 标签名称 */
    private String name;

    /** 标签编码（英文或拼音，方便系统使用） */
    private String code;

    /** 标签颜色（用于前端展示，例如 #FF9900） */
    private String color;

    /** 状态（1=启用，0=禁用） */
    private short status;

    /** 标签权重（用于推荐优先级或排序） */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal weight;
    /** 排序值 */
    private Integer sort;
    //材料组成
    private String composition;
    //产地
    private String placeOfOrigin;
    //重量
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal productWeight;
    //生产日期
    private LocalDateTime productionDate;
    //保质期，单位：天
    private Integer shelfLife;
    /** 标签描述 */
    private String description;
}
