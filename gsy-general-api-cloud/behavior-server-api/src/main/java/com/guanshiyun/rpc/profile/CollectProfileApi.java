package com.guanshiyun.profile;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@FieldNameConstants
public class CollectProfile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    private BigInteger id;
    //sku id
    private BigInteger skuId;
    //收集时间
    private BigInteger collectTime;
}
