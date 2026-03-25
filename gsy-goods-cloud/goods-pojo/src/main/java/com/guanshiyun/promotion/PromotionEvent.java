package com.guanshiyun.promotion;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Document("promotion_event")
@Accessors(chain = true)
public class PromotionEvent extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 活动ID ⭐
    @Id
    private Long id;

    // 活动名称
    private String name;

    // 商家ID
    private Long merchantId;

    // 优惠活动类型  使用枚举
    private Integer type;

    // 活动开始时间
    private LocalDateTime startTime;

    // 活动结束时间
    private LocalDateTime endTime;

    // 活动描述
    private String description;

    // 商品ID
    private Long productId;

    // 活动状态  使用枚举
    private short status;
}
