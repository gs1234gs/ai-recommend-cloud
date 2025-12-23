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
@FieldNameConstants
public class UserCollectSaveApiVO {
    //id
    private BigInteger id;
    //商品id
    private ProductApiVO product;
    //收藏时间
    private LocalDateTime collectTime;
}
