package com.guanshiyun.controller.product.vo;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductSearchVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //商品最低价格
    private BigDecimal minPrice;
    //商品最高价格
    private BigDecimal maxPrice;
    //分类id
    private Long categoryId;
    //搜索内容
    private String searchContent;

}
