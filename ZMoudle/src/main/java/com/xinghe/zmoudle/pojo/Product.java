package com.guanshiyun.product;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 商品信息
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table("product")
public class Product extends BasePojo {
    //商品id
    @Id
    private BigInteger id;
    //商品名称
    private String name;
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
    //分类
    private BigInteger categoryId;
    // 上架时间
    private LocalDateTime publishTime;
    // 下架时间
    private LocalDateTime offlineTime;
}
/**
 * 实体商品，仓库；
 * 关联类：会员，订单，购物车，优惠券，搜索记录，收藏记录，库存，评价，用户行为，商品分类
 *
 * */
