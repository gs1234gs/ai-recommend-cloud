package com.guanshiyun.controller.sku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuStatisticsVO {
    // 总销量
    private Long totalSales = 0L;
    // 商品总数
    private Long totalStock = 0L;
    // 低库存总量
    private Long totalLow = 0L;
    // 总营收
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    // 总价值
    private BigDecimal totalValue = BigDecimal.ZERO;
    // 总销售额
    private BigDecimal totalVolume = BigDecimal.ZERO;
}
