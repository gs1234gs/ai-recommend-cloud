package com.guanshiyun.profile;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class SKUApiVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigInteger id;
    //商品id

    /** 名称*/
    private String name;

    /** 商品编码（内部唯一编码） */
    private String skuCode;

    /** 销售价 */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal price;

    /** 成本价（可选） */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal weight;
    //排序值
    private Integer sort;
    // 上架时间
    private LocalDateTime publishTime;
    // 下架时间
    private LocalDateTime offlineTime;
    // 商品详情
    private String detailContent;
    //商品id
    private BigInteger productId;
    //图片
    private List<String> picList;
}
