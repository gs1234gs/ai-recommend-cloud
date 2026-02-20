package com.guanshiyun.controller.collect.vo;

import com.guanshiyun.profile.ProductApiVO;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
public class UserCollectSaveVO {
    //id
    private BigInteger id;
    //商品id
    private ProductApiVO product;
    //收藏时间
    private LocalDateTime collectTime;
}
