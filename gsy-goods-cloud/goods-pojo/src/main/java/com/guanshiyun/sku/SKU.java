package com.guanshiyun.sku;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * 商品具体销售单元
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sku")
public class SKU extends BasePojo {
    /** SKU 主键ID */
    @Id
    private BigInteger id;

    /** 所属SPU ID（商品标准单元） */
    private Long spuId;

    /** SKU 名称（通常为SPU名 + 规格描述） */
    private String name;

    /** 商品编码（内部唯一编码） */
    private String skuCode;

    /** 条形码（外部编码，可选） */
    private String barCode;

    /** 规格（例如：颜色=黑色；尺寸=128GB） */
    private String spec;

    /** 销售价 */
    private BigDecimal price;

    /** 成本价（可选） */
    private BigDecimal costPrice;

    /** 库存数量 */
    private Integer stock;

    /** 锁定库存（未发货但已下单） */
    private Integer lockedStock;

    /** 商品图片URL */
    private String imageUrl;

    /** 状态（0=下架，1=上架） */
    private short status;

    /** 权重（用于推荐、排序） */
    private BigDecimal weight;

}
