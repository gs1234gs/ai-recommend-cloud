package com.guanshiyun.controller.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistics7DaysVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long totalSales;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private List<DailyTrendVO> dailyTrends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrendVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String date;
        private Long sales;
        private BigDecimal revenue;
        private Long orders;
    }
}
