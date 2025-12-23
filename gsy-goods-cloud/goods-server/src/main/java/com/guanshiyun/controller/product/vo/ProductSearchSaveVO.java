package com.guanshiyun.controller.product.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductSearchSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //商品最低价格
    private BigDecimal minPrice;
    //商品最高价格
    private BigDecimal maxPrice;
    //分类id
    private BigInteger categoryId;
    //搜索内容
    private String searchContent;

}
