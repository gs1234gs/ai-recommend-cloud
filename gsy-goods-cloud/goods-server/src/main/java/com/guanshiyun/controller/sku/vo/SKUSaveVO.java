package com.guanshiyun.controller.sku.vo;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SKUSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    /**要添加的库存*/
    private Integer addStock;
    /** 销售量 */
    private Integer salesVolume;

    /** 状态（1=下架，0=上架） */
    private short status;

    /** 权重（用于推荐、排序） */
    private BigDecimal weight;
    //排序值
    private Integer sort;
    //图片
    private List<String> picList;
    // 上架时间
    private LocalDateTime publishTime;
    // 下架时间
    private LocalDateTime offlineTime;
    // 商品详情
    private String detailContent;
    //商品id
    private Long productId;
}
