package com.guanshiyun.service.sku.impl;

import com.guanshiyun.controller.sku.vo.SkuStatisticsVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.service.sku.SKUStatisticsService;
import com.guanshiyun.sku.SKU;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class SKUStatisticsServiceImpl implements SKUStatisticsService {
    private final SKURepository skuRepository;
    private final MyLong myLong;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<SkuStatisticsVO> getTenantStatistics(String timeRange) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("用户未登录"));
            }
            Long tenantId = myLong.findTenantId(ctx);

            // 定义原生 SQL
            // 注意：给别名加上双引号可以防止某些数据库驱动自动转小写
            String sql = """
                SELECT 
                    COALESCE(SUM(sales_volume), 0) as "totalSales",
                    COALESCE(SUM(stock), 0) as "totalStock",
                    COALESCE(SUM(CASE WHEN stock < 10 THEN 1 ELSE 0 END), 0) as "totalLow",
                    COALESCE(SUM(sales_volume * price), 0) as "totalRevenue",
                    COALESCE(SUM(stock * cost_price), 0) as "totalValue",
                    COALESCE(SUM(sales_volume * price), 0) as "totalVolume"
                FROM sku 
                WHERE tenant_id = :tenantId
                """;

            // 使用 DatabaseClient 执行
            return databaseClient.sql(sql)
                    .bind(SKU.Fields.tenantId, tenantId)
                    .map((row, rowMetadata) -> {
                        // 手动映射结果集到 VO
                        SkuStatisticsVO vo = new SkuStatisticsVO();

                        // 安全获取数值类型：先转为 Number，再转为具体类型
                        // 这样可以避免 BigDecimal/Long/Integer 之间的类型转换异常
                        vo.setTotalSales(((Number) Objects.requireNonNull(row.get("totalSales"))).longValue());
                        vo.setTotalStock(((Number) Objects.requireNonNull(row.get("totalStock"))).longValue());
                        vo.setTotalLow(((Number) Objects.requireNonNull(row.get("totalLow"))).longValue());
                        vo.setTotalRevenue(new BigDecimal(String.valueOf(row.get("totalRevenue"))));
                        vo.setTotalValue(new BigDecimal(String.valueOf(row.get("totalValue"))));
                        vo.setTotalVolume(new BigDecimal(String.valueOf(row.get("totalVolume"))));

                        log.info("统计查询结果: {}", vo);
                        return vo;
                    })
                    .one(); // 聚合查询只返回一行数据
        });
    }

    /**
     * 获取库存预警列表
     */
    @Override
    public Flux<SKU> getLowStockAlerts(Integer threshold, Integer limit) {
        return Flux.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Flux.error(new Throwable("用户未登录"));
            }
            Long tenantId = myLong.findTenantId(ctx);
            return skuRepository.getLowStockSKUs(tenantId, threshold, limit);
        });
    }

    /**
     * 获取热销排行
     */
    @Override
    public Flux<SKU> getTopSellingSKUs(Integer limit) {
        return Flux.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Flux.error(new Throwable("用户未登录"));
            }
            Long tenantId = myLong.findTenantId(ctx);
            return skuRepository.getTopSellingSKUs(tenantId, limit);
        });
    }

    /**
     * 获取滞销排行
     */
    @Override
    public Flux<SKU> getSlowMovingSKUs(Integer days, Integer limit) {
        return Flux.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Flux.error(new Throwable("用户未登录"));
            }
            Long tenantId = myLong.findTenantId(ctx);
            // 滞销排行通常还是基于创建时间或入库时间
            LocalDateTime startTime = LocalDateTime.now().minusDays(days).with(LocalTime.MIN);
            return skuRepository.getSlowMovingSKUs(tenantId, startTime, limit);
        });
    }
}
