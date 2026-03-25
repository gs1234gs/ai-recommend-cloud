package com.guanshiyun.recommendation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import java.time.LocalDateTime;

/**
 * 推荐结果存储
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("recommendation")
public class Recommendation {
    /** 主键ID */
    @Id
    private Long id;

    /** 推荐分数（算法评分或置信度） */
    private Double score;

    /** 是否被用户点击或采纳（0=未点击，1=点击） */
    private short clicked;

    /** 点击时间（如果 clicked=true） */
    private LocalDateTime clickTime;

    /** 曝光时间（推荐展示时间） */
    private LocalDateTime exposureTime;
    //用户id
    private Long userId;
    //商品id
    private String productId;
    //描述
    private String description;
    //推荐时间
    private LocalDateTime recommendTime;
    //创建时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;
}
