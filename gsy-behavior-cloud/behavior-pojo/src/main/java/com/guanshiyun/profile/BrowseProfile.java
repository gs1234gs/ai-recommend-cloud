package com.guanshiyun.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户画像（兴趣、偏好、地理位置等）
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBrowseProfile  {
    /** 主键ID */
    private BigInteger id;
    //创建时间
    private LocalDateTime createTime;
    //sku id
    List<BigInteger> skuId;

}
