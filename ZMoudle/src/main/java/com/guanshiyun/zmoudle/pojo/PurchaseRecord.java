package com.guanshiyun.zmoudle.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRecord {
    private Long userId;
    private Long productId;
    private Integer count;

}
