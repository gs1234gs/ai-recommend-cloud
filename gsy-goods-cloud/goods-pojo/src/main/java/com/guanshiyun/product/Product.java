package com.guanshiyun.product;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * 商品信息
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("product")
public class Product extends BasePojo {
    //商品id
    @Id
    private BigInteger id;
    //商品名称
    private String name;
    //商品价格
    private BigDecimal price;
    //商品描述
    private String description;
    //商品图片
    private String image;
    //视频
    private String video;
    //品牌
    private String brand;
    //产地
    private String placeOfOrigin;
    //商品等级，
    private short level;
}
/**
 * 实体商品，仓库；
 * 关联类：会员，订单，购物车，优惠券，搜索记录，收藏记录，库存，评价，用户行为，商品分类
 *
 * */
