package com.guanshiyun.controller.product.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductCountVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //商品总数
    private Integer productCount;
    //上架商品数
    private Integer publishCount;
    //在售商品
    private Integer sellingCount;
    //总库存
    private Integer totalStock;
    //总销量
    private Integer totalSalesVolume;
    //平均销量
    private Integer averageSalesVolume;
}
