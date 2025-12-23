package com.guanshiyun.rpc.apisave;


import com.guanshiyun.publicbehaviorvo.ProductApiVO;
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
public class UserClickSaveApiVO {
    private BigInteger id;
    //点击时间
    private LocalDateTime clickTime;
    //点击标签id
    private BigInteger tagId;
    //点击
    private ProductApiVO product;
}
