package com.guanshiyun.service.product.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BehaviorRecord {
    private LocalDateTime time;
    private BigInteger productId;
    private String type; // "click", "collect", "purchase", "browse", "search"
    private Object origin;
}
