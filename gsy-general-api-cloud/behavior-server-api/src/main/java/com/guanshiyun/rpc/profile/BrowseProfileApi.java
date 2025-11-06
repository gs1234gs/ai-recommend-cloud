package com.guanshiyun.rpc.profile;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户画像（兴趣、偏好、地理位置等）
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Builder
@ToString(callSuper = true)
public class BrowseProfileApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    private BigInteger id;
    //sku id
    List<BigInteger> skuId;
    //浏览开始时间
    private LocalDateTime browseStartTime;
    //浏览结束时间
    private LocalDateTime browseEndTime;

}
