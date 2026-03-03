package com.guanshiyun.controller.product.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.guanshiyun.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductCustomerVO {
    //商品id
    private BigInteger id;
    //商品名称
    private String name;
    //商品原价
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal originalPrice;
    //商品优惠后价格
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal discountPrice;
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
    // 库存
    private Integer stock;
    // 销量
    private Integer salesVolume;
    //商品状态，0=下架，1=上架,2=预发布
    private short status;
    /**
     * 上架时间
     */
    private LocalDateTime publishTime;
    /**
     * 下架时间
     */
    private LocalDateTime offlineTime;
    //标签名称
    private String tagName;
    //最低价格
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal minPrice;
    //最高价格
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal maxPrice;

    // 构建 VO 的方法复用
    public static ProductCustomerVO toVO(Product p) {
        return ProductCustomerVO.builder()
                .id(p.getId())
                .name(p.getName())
                .image(p.getImage())
                .video(p.getVideo())
                .status(p.getStatus())
                .description(p.getDescription())
                .publishTime(p.getPublishTime())
                .brand(p.getBrand())
                .level(p.getLevel())
                .placeOfOrigin(p.getPlaceOfOrigin())
                .minPrice(Optional.ofNullable(p.getMinPrice())
                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .maxPrice(Optional.ofNullable(p.getMaxPrice())
                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .originalPrice(Optional.ofNullable(p.getMaxPrice())
                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .discountPrice(Optional.ofNullable(p.getMinPrice())
                        .map(price -> price.multiply(new BigDecimal("0.7"))
                                .setScale(2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .build();
    }

}
