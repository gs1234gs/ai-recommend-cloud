package com.guanshiyun.controller.sku.vo;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SKUVO {
    private BigInteger id;
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
    /**要添加的库存*/
    private Integer addStock;
    /** 销售量 */
    private Integer salesVolume;

    /** 状态（0=下架，1=上架） */
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
}
