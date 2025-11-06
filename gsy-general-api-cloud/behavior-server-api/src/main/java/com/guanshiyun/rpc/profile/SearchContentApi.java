package com.guanshiyun.profile;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@FieldNameConstants
public class SearchContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //搜索内容id
    private BigInteger id;
    //搜索内容
    private String content;
    // 最高价格
    private BigDecimal maxPrice;
    //最低价格
    private BigDecimal minPrice;
    //品牌id等
    private BigInteger brandId;
    /**商品类别 */
    private Integer categoryId;
    //搜索时间
    private BigInteger searchTime;
}
