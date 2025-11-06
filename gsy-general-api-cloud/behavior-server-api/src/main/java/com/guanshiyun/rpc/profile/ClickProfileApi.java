package com.guanshiyun.rpc.profile;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@FieldNameConstants
public class ClickProfileApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //用户点击画像
    private BigInteger id;
    //点击时间
    private LocalDateTime clickTime;
    //点击标签id
    private BigInteger tagId;
    //点击skuId
    private BigInteger skuId;
}
