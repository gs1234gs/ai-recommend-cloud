package com.guanshiyun.profile;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class TagApiVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private BigInteger id;
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
