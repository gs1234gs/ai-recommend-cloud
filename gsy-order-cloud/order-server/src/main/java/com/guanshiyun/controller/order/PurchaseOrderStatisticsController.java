package com.guanshiyun.controller.order;

import com.guanshiyun.controller.order.vo.*;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.order.PurchaseOrderStatisticsService;
import com.guanshiyun.service.order.impl.PurchaseOrderStatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purchaseStatistics/")
@RequiredArgsConstructor
public class PurchaseOrderStatisticsController {
    private final PurchaseOrderStatisticsService orderStatisticsService;

    /**
     * 获取订单统计概览
     * 参考 SKU 模式，只传一个 timeRange 参数
     */
    @GetMapping("/overview")
    public Mono<ResultT<OrderStatisticsVO>> getOverview(
            @RequestParam(defaultValue = "7d") String timeRange,
            @RequestParam(required = false) String startTime, // 可选：用于趋势图起始时间
            @RequestParam(required = false) String endTime) { // 可选：用于趋势图结束时间

        // 构建 DTO
        OrderStatisticsQueryDTO queryDTO = OrderStatisticsQueryDTO.builder()
                .timeRange(timeRange)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        return orderStatisticsService.getOrderStatistics( queryDTO) // 租户ID在Service层从上下文获取
                .map(ResultT::success);
    }

    /**
     * 查询近7天订单统计（水波图专用）
     * 接口路径：/purchaseStatistics/7days
     */
    @GetMapping("/7days")
    public Mono<ResultT<OrderStatistics7DaysVO>> getLast7DaysStats() {
        return orderStatisticsService.getOrderStatistics7Days()
                .map(ResultT::success);
    }

    /**
     * 查询近30天订单统计（水波图专用）
     * 接口路径：/purchaseStatistics/30days
     */
    @GetMapping("/30days")
    public Mono<ResultT<OrderStatistics30DaysVO>> getLast30DaysStats() {
        return orderStatisticsService.getOrderStatistics30Days()
                .map(ResultT::success);
    }

    /**
     * 查询近12个月订单统计（水波图专用）
     * 接口路径：/purchaseStatistics/12months
     */
    @GetMapping("/12months")
    public Mono<ResultT<OrderStatistics12MonthsVO>> getLast12MonthsStats() {
        return orderStatisticsService.getOrderStatistics12Months()
                .map(ResultT::success);
    }
    //下单趋势统计
    @GetMapping("/orderTrend")
    public Mono<ResultT<PurchaseOrderStatisticsServiceImpl.HourlyHeatmapVO>> getOrderTrend(@RequestParam String rangeType) {
        return orderStatisticsService.getHourlyOrderHeatmap(rangeType)
                .map(ResultT::success);
    }
}
