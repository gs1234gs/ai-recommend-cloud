package com.guanshiyun.controller.sku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SKUGroupByProductIdVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long productId;
    private String productName;
    private List<SKUVO> skuList;
}
