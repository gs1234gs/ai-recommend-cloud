package com.guanshiyun.sku;

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

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;

/**
 *
 * 商品具体销售单元
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("sku")
@Accessors(chain = true)
public class SKU extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** SKU 主键ID */
    @Id
    private Long id;
    //商品id

    /** 名称*/
    private String name;

    /** 商品编码（内部唯一编码） */
    private String skuCode;

    /** 销售价 */
    private BigDecimal price;

    /** 成本价（可选） */
    private BigDecimal costPrice;

    /** 库存数量 */
    private Integer stock;
    /** 销售量 */
    private Integer salesVolume;

    /** 是否下架（0=否，1=是） */
    private short status;

    /** 权重（用于推荐、排序） */
    private BigDecimal weight;
    //排序值
    private Integer sort;
    // 上架时间
    private LocalDateTime publishTime;
    // 下架时间
    private LocalDateTime offlineTime;
    // 商品详情
    private String detailContent;
    //图片
    private Object picList;
    //商品id
    private Long productId;
    //租户id
    private Long tenantId;

}
