package com.guanshiyun.controller.search.vo;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class UserSearchVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
