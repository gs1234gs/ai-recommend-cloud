package com.guanshiyun.controller.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticsVO {
    // 总销量 (对应SQL中的 as totalSales)
    private Long totalSales = 0L;
    // 总营收 (对应SQL中的 as totalRevenue)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    // 总订单数 (对应SQL中的 as totalOrders)
    private Long totalOrders = 0L;

    // 时间维度统计
    private TimeStatisticsVO last7Days;
    private TimeStatisticsVO last30Days;
    private TimeStatisticsVO last365Days;

    // 趋势数据
    private List<DailyTrendVO> dailyTrends;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TimeStatisticsVO {
        // 销量 (对应SQL中的 as sales)
        private Long sales;
        // 营收 (对应SQL中的 as revenue)
        private BigDecimal revenue;
        // 订单数 (对应SQL中的 as orders)
        private Long orders;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyTrendVO {
        // 日期 (对应SQL中的 as date)
        private String date;
        // 销量 (对应SQL中的 as sales)
        private Long sales;
        // 营收 (对应SQL中的 as revenue)
        private BigDecimal revenue;
        // 订单数 (对应SQL中的 as orders)
        private Long orders;
    }
}