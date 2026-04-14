package com.guanshiyun.service.sku;

import com.guanshiyun.controller.sku.vo.SkuStatisticsVO;
import com.guanshiyun.sku.SKU;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SKUStatisticsService {
    Mono<SkuStatisticsVO> getTenantStatistics(String timeRange);
    Flux<SKU> getLowStockAlerts(Integer threshold, Integer limit);
    Flux<SKU> getTopSellingSKUs( Integer limit);
    Flux<SKU> getSlowMovingSKUs( Integer days, Integer limit);
}
