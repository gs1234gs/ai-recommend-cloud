package com.guanshiyun.service.order.impl;

import com.guanshiyun.controller.order.vo.*;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.order.PurChaseOrderRepository;
import com.guanshiyun.service.order.PurchaseOrderStatisticsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderStatisticsServiceImpl implements PurchaseOrderStatisticsService {

    private final PurChaseOrderRepository orderRepository;
    private final MyLong myLong;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<OrderStatisticsVO> getOrderStatistics( OrderStatisticsQueryDTO queryDTO) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("用户未登录"));
            }
            Long contextTenantId = myLong.findTenantId(ctx);

            // 1. 解析时间范围
            LocalDateTime endTime = LocalDateTime.now();
            if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
                endTime = LocalDateTime.parse(queryDTO.getEndTime() + "T23:59:59", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            endTime = endTime.with(LocalTime.MAX);

            int days = parseDays(queryDTO.getTimeRange());
            LocalDateTime periodStartTime = endTime.minusDays(days - 1).with(LocalTime.MIN);

            LocalDateTime trendStartTime = periodStartTime;
            if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
                trendStartTime = LocalDateTime.parse(queryDTO.getStartTime() + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // 2. 使用 DatabaseClient 并行查询
            // 2.1 总统计 SQL
            String totalSql = """
                SELECT 
                    COALESCE(SUM(num), 0) as "totalSales",
                    COALESCE(SUM(pay_amount), 0) as "totalRevenue",
                    COUNT(*) as "totalOrders"
                FROM purchase_order 
                WHERE tenant_id = :tenantId AND status != 6
                """;

            // 2.2 指定周期统计 SQL
            String periodSql = """
                SELECT 
                    COALESCE(SUM(num), 0) as "sales",
                    COALESCE(SUM(pay_amount), 0) as "revenue",
                    COUNT(*) as "orders"
                FROM purchase_order 
                WHERE tenant_id = :tenantId
                AND status != 6
                AND order_placement_time >= :startTime
                AND order_placement_time <= :endTime
                """;

            // 2.3 每日趋势 SQL
            String trendSql = """
                SELECT 
                    DATE(order_placement_time) as "date",
                    COALESCE(SUM(num), 0) as "sales",
                    COALESCE(SUM(pay_amount), 0) as "revenue",
                    COUNT(*) as "orders"
                FROM purchase_order 
                WHERE tenant_id = :tenantId
                AND status != 6
                AND order_placement_time >= :startTime
                AND order_placement_time <= :endTime
                GROUP BY DATE(order_placement_time)
                ORDER BY DATE(order_placement_time)
                """;

            // 3. 执行查询并组装
            Mono<OrderStatisticsVO> totalStatsMono = databaseClient.sql(totalSql)
                    .bind("tenantId", contextTenantId)
                    .map((row, rowMetadata) -> {
                        OrderStatisticsVO vo = new OrderStatisticsVO();
                        vo.setTotalSales(((Number) row.get("totalSales")).longValue());
                        // 处理 BigDecimal
                        vo.setTotalRevenue(new BigDecimal(String.valueOf(row.get("totalRevenue"))));
                        vo.setTotalOrders(((Number) row.get("totalOrders")).longValue());
                        return vo;
                    })
                    .one();

            Mono<OrderStatisticsVO.TimeStatisticsVO> periodStatsMono = databaseClient.sql(periodSql)
                    .bind("tenantId", contextTenantId)
                    .bind("startTime", periodStartTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> {
                        OrderStatisticsVO.TimeStatisticsVO stats = new OrderStatisticsVO.TimeStatisticsVO();
                        stats.setSales(((Number) Objects.requireNonNull(row.get("sales"))).longValue());
                        stats.setRevenue(new BigDecimal(String.valueOf(row.get("revenue"))));
                        stats.setOrders(((Number) Objects.requireNonNull(row.get("orders"))).longValue());
                        return stats;
                    })
                    .one();

            Flux<OrderStatisticsVO.DailyTrendVO> trendsFlux = databaseClient.sql(trendSql)
                    .bind("tenantId", contextTenantId)
                    .bind("startTime", trendStartTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> {
                        OrderStatisticsVO.DailyTrendVO trend = new OrderStatisticsVO.DailyTrendVO();
                        // 处理日期类型，数据库返回的可能是 LocalDate 或 String
                        Object dateObj = row.get("date");
                        if (dateObj instanceof LocalDate) {
                            trend.setDate(((LocalDate) dateObj).toString());
                        } else {
                            trend.setDate(String.valueOf(LocalDate.parse(String.valueOf(dateObj))));
                        }
                        trend.setSales(((Number) row.get("sales")).longValue());
                        trend.setRevenue(new BigDecimal(String.valueOf(row.get("revenue"))));
                        trend.setOrders(((Number) row.get("orders")).longValue());
                        return trend;
                    })
                    .all();

            return Mono.zip(totalStatsMono, periodStatsMono, trendsFlux.collectList())
                    .map(tuple -> {
                        OrderStatisticsVO vo = tuple.getT1();
                        OrderStatisticsVO.TimeStatisticsVO periodStats = tuple.getT2();
                        List<OrderStatisticsVO.DailyTrendVO> trends = tuple.getT3();

                        // 4. 根据 timeRange 填充数据
                        if ("7d".equalsIgnoreCase(queryDTO.getTimeRange())) {
                            vo.setLast7Days(periodStats);
                        } else if ("30d".equalsIgnoreCase(queryDTO.getTimeRange())) {
                            vo.setLast30Days(periodStats);
                        } else if ("90d".equalsIgnoreCase(queryDTO.getTimeRange())) {
                            // 如果有对应字段请开启
                             vo.setLast365Days(periodStats);
                        }

                        vo.setDailyTrends(trends);
                        return vo;
                    });
        });
    }

    private int parseDays(String timeRange) {
        if (timeRange == null) return 7;
        return switch (timeRange.toLowerCase()) {
            case "7d" -> 7;
            case "30d" -> 30;
            case "90d" -> 90;
            case "1y", "365d" -> 365;
            default -> 7;
        };
    }


    /**
     * 查询近7天订单统计
     */
    @Override
    public Mono<OrderStatistics7DaysVO> getOrderStatistics7Days() {


            return getOrderStatisticsForPeriod(7, "day");


    }

    /**
     * 查询近1个月订单统计
     */
    @Override
    public Mono<OrderStatistics30DaysVO> getOrderStatistics30Days() {

            return getOrderStatisticsForPeriod( 30, "day");

    }

    /**
     * 查询近12个月订单统计
     */
    @Override
    public Mono<OrderStatistics12MonthsVO> getOrderStatistics12Months() {

            return getOrderStatisticsForPeriod( 12, "month");
    }

    /**
     * 通用方法：根据周期和粒度获取统计数据
     * @param daysOrMonths 时间范围长度
     * @param granularity 时间粒度 ("day" 或 "month")
     */
    private <T> Mono<T> getOrderStatisticsForPeriod( int daysOrMonths, String granularity) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("用户未登录"));
            }
            Long tenantId = myLong.findTenantId(ctx);

            LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);
            LocalDateTime startTime;

            if ("month".equalsIgnoreCase(granularity)) {
                // 计算近N个月的开始时间
                startTime = endTime.minusMonths(daysOrMonths - 1).with(LocalTime.MIN);
                // 为了精确，我们减去月份后，再调整到当月的第一天
                startTime = startTime.withDayOfMonth(1);
            } else {
                // 计算近N天的开始时间
                startTime = endTime.minusDays(daysOrMonths - 1).with(LocalTime.MIN);
            }

            // SQL 查询部分保持不变
            String totalSql = """
                SELECT 
                    COALESCE(SUM(num), 0) as "totalSales",
                    COALESCE(SUM(pay_amount), 0) as "totalRevenue",
                    COUNT(*) as "totalOrders"
                FROM purchase_order 
                WHERE tenant_id = :tenantId AND status != 6
                """;

            String periodSql = """
                SELECT 
                    COALESCE(SUM(num), 0) as "periodSales",
                    COALESCE(SUM(pay_amount), 0) as "periodRevenue",
                    COUNT(*) as "periodOrders"
                FROM purchase_order 
                WHERE tenant_id = :tenantId
                AND status != 6
                AND order_placement_time >= :startTime
                AND order_placement_time <= :endTime
                """;

            String trendSql;
            if ("month".equalsIgnoreCase(granularity)) {
                trendSql = """
                    SELECT 
                        DATE_FORMAT(order_placement_time, '%Y-%m') as "period",
                        COALESCE(SUM(num), 0) as "sales",
                        COALESCE(SUM(pay_amount), 0) as "revenue",
                        COUNT(*) as "orders"
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId
                    AND status != 6
                    AND order_placement_time >= :startTime
                    AND order_placement_time <= :endTime
                    GROUP BY DATE_FORMAT(order_placement_time, '%Y-%m')
                    ORDER BY DATE_FORMAT(order_placement_time, '%Y-%m')
                    """;
            } else {
                trendSql = """
                    SELECT 
                        DATE(order_placement_time) as "period",
                        COALESCE(SUM(num), 0) as "sales",
                        COALESCE(SUM(pay_amount), 0) as "revenue",
                        COUNT(*) as "orders"
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId
                    AND status != 6
                    AND order_placement_time >= :startTime
                    AND order_placement_time <= :endTime
                    GROUP BY DATE(order_placement_time)
                    ORDER BY DATE(order_placement_time)
                    """;
            }

            // 执行查询
            Mono<Long> totalSalesMono = databaseClient.sql(totalSql)
                    .bind("tenantId", tenantId)
                    .map((row, rowMetadata) -> ((Number) row.get("totalSales")).longValue())
                    .one();

            Mono<BigDecimal> totalRevenueMono = databaseClient.sql(totalSql)
                    .bind("tenantId", tenantId)
                    .map((row, rowMetadata) -> new BigDecimal(String.valueOf(row.get("totalRevenue"))))
                    .one();

            Mono<Long> totalOrdersMono = databaseClient.sql(totalSql)
                    .bind("tenantId", tenantId)
                    .map((row, rowMetadata) -> ((Number) row.get("totalOrders")).longValue())
                    .one();

            // 周期内统计
            Mono<Long> periodSalesMono = databaseClient.sql(periodSql)
                    .bind("tenantId", tenantId)
                    .bind("startTime", startTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> ((Number) row.get("periodSales")).longValue())
                    .one();

            Mono<BigDecimal> periodRevenueMono = databaseClient.sql(periodSql)
                    .bind("tenantId", tenantId)
                    .bind("startTime", startTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> new BigDecimal(String.valueOf(row.get("periodRevenue"))))
                    .one();

            Mono<Long> periodOrdersMono = databaseClient.sql(periodSql)
                    .bind("tenantId", tenantId)
                    .bind("startTime", startTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> ((Number) row.get("periodOrders")).longValue())
                    .one();

            // 趋势数据
            Flux<TrendItem> trendsFlux = databaseClient.sql(trendSql)
                    .bind("tenantId", tenantId)
                    .bind("startTime", startTime)
                    .bind("endTime", endTime)
                    .map((row, rowMetadata) -> {
                        TrendItem item = new TrendItem();
                        item.period = String.valueOf(row.get("period"));
                        item.sales = ((Number) Objects.requireNonNull(row.get("sales"))).longValue();
                        item.revenue = new BigDecimal(String.valueOf(row.get("revenue")));
                        item.orders = ((Number) Objects.requireNonNull(row.get("orders"))).longValue();
                        return item;
                    })
                    .all();

            // 组装最终结果
            return Mono.zip(totalSalesMono, totalRevenueMono, totalOrdersMono,
                            periodSalesMono, periodRevenueMono, periodOrdersMono,
                            trendsFlux.collectList())
                    .map(tuple -> {
                        Long totalSales = tuple.getT1();
                        BigDecimal totalRevenue = tuple.getT2();
                        Long totalOrders = tuple.getT3();
                        Long periodSales = tuple.getT4();
                        BigDecimal periodRevenue = tuple.getT5();
                        Long periodOrders = tuple.getT6();
                        List<TrendItem> trends = tuple.getT7();

                        if ("month".equalsIgnoreCase(granularity)) {
                            // 构建12个月统计VO
                            OrderStatistics12MonthsVO vo = new OrderStatistics12MonthsVO();
                            vo.setTotalSales(totalSales);
                            vo.setTotalRevenue(totalRevenue);
                            vo.setTotalOrders(totalOrders);
                            // 这里我们只返回周期内的总和，如果需要更复杂的结构可以调整
                            // 为简化，这里将周期总和也作为总览的一部分，实际业务可能不同
                            vo.setMonthlyTrends(trends.stream().map(t -> {
                                OrderStatistics12MonthsVO.MonthlyTrendVO trendVO = new OrderStatistics12MonthsVO.MonthlyTrendVO();
                                trendVO.setMonth(t.period);
                                trendVO.setSales(t.sales);
                                trendVO.setRevenue(t.revenue);
                                trendVO.setOrders(t.orders);
                                return trendVO;
                            }).collect(java.util.stream.Collectors.toList()));
                            return (T) vo;
                        } else {
                            // 构建7天或30天统计VO
                            if (daysOrMonths == 7) {
                                OrderStatistics7DaysVO vo = new OrderStatistics7DaysVO();
                                vo.setTotalSales(totalSales);
                                vo.setTotalRevenue(totalRevenue);
                                vo.setTotalOrders(totalOrders);
                                vo.setDailyTrends(trends.stream().map(t -> {
                                    OrderStatistics7DaysVO.DailyTrendVO trendVO = new OrderStatistics7DaysVO.DailyTrendVO();
                                    trendVO.setDate(t.period);
                                    trendVO.setSales(t.sales);
                                    trendVO.setRevenue(t.revenue);
                                    trendVO.setOrders(t.orders);
                                    return trendVO;
                                }).collect(java.util.stream.Collectors.toList()));
                                return (T) vo;
                            } else { // 30 days
                                OrderStatistics30DaysVO vo = new OrderStatistics30DaysVO();
                                vo.setTotalSales(totalSales);
                                vo.setTotalRevenue(totalRevenue);
                                vo.setTotalOrders(totalOrders);
                                vo.setDailyTrends(trends.stream().map(t -> {
                                    OrderStatistics30DaysVO.DailyTrendVO trendVO = new OrderStatistics30DaysVO.DailyTrendVO();
                                    trendVO.setDate(t.period);
                                    trendVO.setSales(t.sales);
                                    trendVO.setRevenue(t.revenue);
                                    trendVO.setOrders(t.orders);
                                    return trendVO;
                                }).collect(java.util.stream.Collectors.toList()));
                                return (T) vo;
                            }
                        }
                    });
        });
    }

    // 内部类，用于临时存储趋势数据
    private static class TrendItem {
        String period;
        Long sales;
        BigDecimal revenue;
        Long orders;
    }


    // 1. 新增接口实现方法
    @Override
    public Mono<HourlyHeatmapVO> getHourlyOrderHeatmap(String rangeType) {
        return Mono.deferContextual(ctx -> {
            // 1. 权限校验
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Throwable("用户未登录"));
            }
            Long contextTenantId = myLong.findTenantId(ctx);

            // 2. 计算时间范围 & 确定粒度
            LocalDateTime endTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime startTime;
            String unit; // 返回给前端的单位标识
            String sql;  // 动态SQL

            // 标准化输入
            String type = rangeType.toLowerCase();

            switch (type) {
                case "1d":
                    // --- 粒度：小时 ---
                    startTime = endTime.minusDays(1);
                    unit = "hour";
                    sql = """
                    SELECT 
                        HOUR(order_placement_time) as time_key, 
                        COUNT(*) as order_count 
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId 
                      AND status != 6 
                      AND order_placement_time >= :startTime 
                      AND order_placement_time <= :endTime 
                    GROUP BY HOUR(order_placement_time)
                    """;
                    break;

                case "7d":
                case "30d":
                    // --- 粒度：天 ---
                    int days = type.equals("7d") ? 7 : 30;
                    startTime = endTime.minusDays(days);
                    unit = "day";
                    // 统计每天的订单量，注意格式化为 yyyy-MM-dd
                    sql = """
                    SELECT 
                        DATE_FORMAT(order_placement_time, '%Y-%m-%d') as time_key, 
                        COUNT(*) as order_count 
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId 
                      AND status != 6 
                      AND order_placement_time >= :startTime 
                      AND order_placement_time <= :endTime 
                    GROUP BY DATE_FORMAT(order_placement_time, '%Y-%m-%d')
                    """;
                    break;

                case "12m":
                case "1y": // 兼容 1年
                    // --- 粒度：月 ---
                    startTime = endTime.minusMonths(12); // 近12个月
                    unit = "month";
                    // 统计每月的订单量，格式化为 MM (月份数字)
                    sql = """
                    SELECT 
                        DATE_FORMAT(order_placement_time, '%m') as time_key, 
                        COUNT(*) as order_count 
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId 
                      AND status != 6 
                      AND order_placement_time >= :startTime 
                      AND order_placement_time <= :endTime 
                    GROUP BY DATE_FORMAT(order_placement_time, '%m')
                    """;
                    break;

                default:
                    // 默认兜底，比如返回7天
                    startTime = endTime.minusDays(7);
                    unit = "day";
                    sql = """
                    SELECT 
                        DATE_FORMAT(order_placement_time, '%Y-%m-%d') as time_key, 
                        COUNT(*) as order_count 
                    FROM purchase_order 
                    WHERE tenant_id = :tenantId 
                      AND status != 6 
                      AND order_placement_time >= :startTime 
                      AND order_placement_time <= :endTime 
                    GROUP BY DATE_FORMAT(order_placement_time, '%Y-%m-%d')
                    """;
            }

            // 3. 执行数据库查询
            Flux<Map<String, Object>> resultFlux = databaseClient.sql(sql)
                    .bind("tenantId", contextTenantId)
                    .bind("startTime", startTime)
                    .bind("endTime", endTime)
                    .map((row, metadata) -> {
                        Map<String, Object> map = new HashMap<>();
                        // 统一处理 key (数据库返回的可能是 String 或 Number)
                        Object keyObj = row.get("time_key");
                        String keyStr = keyObj != null ? keyObj.toString() : "0";

                        // 修复月份格式：如果单位是月且 key 是 1-9，补零 (01, 02...)
                        if ("month".equals(unit) && keyStr.length() == 1) {
                            keyStr = "0" + keyStr;
                        }

                        map.put("timeKey", keyStr);

                        Number countNum = (Number) row.get("order_count");
                        map.put("count", countNum != null ? countNum.longValue() : 0L);
                        return map;
                    })
                    .all();

            // 4. 内存中组装数据 (补全缺失的时间点)
            return resultFlux.collectList()
                    .map(list -> {
                        Map<String, Long> resultMap = new HashMap<>();

                        // 4.1 初始化 Map，填入默认值 (0)
                        if ("hour".equals(unit)) {
                            // 补全 0-23 小时
                            for (int i = 0; i < 24; i++) {
                                resultMap.put(String.valueOf(i), 0L);
                            }
                        } else if ("day".equals(unit)) {
                            // 补全 startTime 到 endTime 之间的每一天
                            LocalDateTime cursor = startTime;
                            while (cursor.isBefore(endTime) || cursor.isEqual(endTime)) {
                                String dateStr = cursor.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                resultMap.put(dateStr, 0L);
                                cursor = cursor.plusDays(1);
                            }
                        } else if ("month".equals(unit)) {
                            // 补全 01-12 月
                            for (int i = 1; i <= 12; i++) {
                                resultMap.put(String.format("%02d", i), 0L);
                            }
                        }

                        // 4.2 覆盖数据库查到的真实数据
                        for (Map<String, Object> item : list) {
                            String timeKey = (String) item.get("timeKey");
                            Long count = (Long) item.get("count");
                            // 防止数据库返回的 key 和我们生成的 key 格式不一致 (如月份 1 vs 01)
                            resultMap.merge(timeKey, count, Long::sum);
                        }

                        // 4.3 排序并转换为 VO
                        List<HourlyHeatmapVO.TimeData> finalData = resultMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(entry -> new HourlyHeatmapVO.TimeData(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList());

                        HourlyHeatmapVO vo = new HourlyHeatmapVO();
                        vo.setRangeType(rangeType);
                        vo.setUnit(unit);
                        vo.setData(finalData);
                        return vo;
                    });
        });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyHeatmapVO {
        private String rangeType;   // 范围类型: 1d, 7d, 30d, 12m
        private String unit;        // 数据单位: "hour", "day", "month"
        private List<TimeData> data; // 时间序列数据

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TimeData {
            private String timeKey; // 键：可能是 "0-23", "2023-10-01", 或 "10" (月)
            private Long count;     // 数量
        }
    }
}