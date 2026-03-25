package com.guanshiyun.controller.click.vo;

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
public class UserClickSaveVO {
    private Long id;
    //点击时间
    private LocalDateTime clickTime;
    //点击标签id
    private Long tagId;
    //点击
    private ProductApiVO product;
}
