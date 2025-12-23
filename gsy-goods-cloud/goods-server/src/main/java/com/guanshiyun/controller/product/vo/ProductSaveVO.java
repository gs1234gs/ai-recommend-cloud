package com.guanshiyun.controller.product.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaveVO {
    //商品id
    private BigInteger id;
    //商品名称
    private String name;
    //商品价格
    @JsonSerialize(using = ToStringSerializer.class)
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
    private Integer stock;            // 库存
    private Integer salesVolume;      // 销量
    //商品状态，0=下架，1=上架
    private short status;
    private LocalDateTime publishTime; // 上架时间
    private LocalDateTime offlineTime; // 下架时间
    //仓库id
    private List<BigInteger> warehouseId;
    //分类 id
    private List<BigInteger> categoryId;
    //标签 id
    private List<BigInteger> tagId;
    //sku列表
    private List<BigInteger> skuList;

}
