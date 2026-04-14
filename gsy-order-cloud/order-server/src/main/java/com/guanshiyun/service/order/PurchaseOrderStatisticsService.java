package com.guanshiyun.service.order;

import com.guanshiyun.controller.order.vo.*;
import com.guanshiyun.service.order.impl.PurchaseOrderStatisticsServiceImpl;
import reactor.core.publisher.Mono;

public interface PurchaseOrderStatisticsService {
    Mono<OrderStatisticsVO> getOrderStatistics(OrderStatisticsQueryDTO queryDTO);
    // 新增的方法
    Mono<OrderStatistics7DaysVO> getOrderStatistics7Days();

    Mono<OrderStatistics30DaysVO> getOrderStatistics30Days();

    Mono<OrderStatistics12MonthsVO> getOrderStatistics12Months();
    Mono<PurchaseOrderStatisticsServiceImpl.HourlyHeatmapVO> getHourlyOrderHeatmap( String rangeType);

}
