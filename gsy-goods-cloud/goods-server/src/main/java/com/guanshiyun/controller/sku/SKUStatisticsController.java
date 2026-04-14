package com.guanshiyun.controller.sku;

import com.guanshiyun.controller.sku.vo.SkuStatisticsVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sku.SKUStatisticsService;
import com.guanshiyun.sku.SKU;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/sku/statistics")
@RequiredArgsConstructor
public class SKUStatisticsController {
    private final SKUStatisticsService skuStatisticsService;

    /**
     * 获取租户统计概览
     */
    @GetMapping("/overview")
    public Mono<ResultT<SkuStatisticsVO>> getOverview(
            @RequestParam(defaultValue = "all") String timeRange) {

        return skuStatisticsService.getTenantStatistics( timeRange)
                .map(ResultT::success);
    }

    /**
     * 获取库存预警
     */
    @GetMapping("/alerts/low-stock")
    public Mono<ResultT<List<SKU>>> getLowStockAlerts(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(defaultValue = "20") Integer limit) {



        return skuStatisticsService.getLowStockAlerts( threshold, limit)
                .collectList()
                .map(ResultT::success);
    }

    /**
     * 获取热销排行
     */
    @GetMapping("/rankings/top-selling")
    public Mono<ResultT<List<SKU>>> getTopSellingRanking(
            @RequestParam(defaultValue = "10") Integer limit) {



        return skuStatisticsService.getTopSellingSKUs( limit)
                .collectList()
                .map(ResultT::success);
    }

    /**
     * 获取滞销排行
     */
    @GetMapping("/rankings/slow-moving")
    public Mono<ResultT<List<SKU>>> getSlowMovingRanking(
            @RequestParam(defaultValue = "30") Integer days,
            @RequestParam(defaultValue = "10") Integer limit) {

        return skuStatisticsService.getSlowMovingSKUs(days, limit)
                .collectList()
                .map(ResultT::success);
    }
}
