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
public class StatisticsVO {
    //销量
    private Long salesCount;
    //营收
    private BigDecimal revenue;
    //销售额
    private BigDecimal salesVolume;
}
