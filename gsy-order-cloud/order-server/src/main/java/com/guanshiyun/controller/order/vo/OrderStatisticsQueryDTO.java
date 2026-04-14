package com.guanshiyun.controller.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticsQueryDTO {
    /**
     * 时间范围标识
     * 例如："7d", "30d", "90d", "1y"
     */
    private String timeRange;

    /**
     * 趋势图开始时间 (可选，默认根据 timeRange 计算)
     */
    private String startTime;

    /**
     * 趋势图结束时间 (可选，默认当前时间)
     */
    private String endTime;
}
