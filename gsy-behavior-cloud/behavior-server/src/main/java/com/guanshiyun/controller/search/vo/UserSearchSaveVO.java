package com.guanshiyun.controller.search.vo;

import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
public class UserSearchSaveVO {
    //会话 id
    private Long id;
    //搜索内容
    private String searchContent;
    // 最高价格
    private BigDecimal maxPrice;
    //最低价格
    private BigDecimal minPrice;
    //品牌id等
    private Long brandId;
    //搜索时间
    private LocalDateTime searchTime;
}
