package com.guanshiyun.zmoudle.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 商品信息
 * */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Product  {
    //商品id
    private Long id;
    //商品名称
    private String name;
    //价格
    private BigDecimal price;
}
/**
 * 实体商品，仓库；
 * 关联类：会员，订单，购物车，优惠券，搜索记录，收藏记录，库存，评价，用户行为，商品分类
 *
 * */
