package com.guanshiyun.controller.collect.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guanshiyun.profile.ProductApiVO;
import lombok.*;
import lombok.experimental.FieldNameConstants;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCollectSaveVO {
    //id
    private Long id;
    //商品id
    private ProductApiVO product;
    //收藏时间
    private LocalDateTime collectTime;
}
