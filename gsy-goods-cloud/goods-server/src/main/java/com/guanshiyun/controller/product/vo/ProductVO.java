package com.guanshiyun.controller.product.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
    private BigInteger warehouseId;
    //分类 id
    private List<BigInteger> categoryId;
    //标签 id
    private List<BigInteger> tagId;
    //创建开始时间
    private LocalDateTime startTime;
    //创建结束时间
    private LocalDateTime endTime;
    //最低价格
    private BigDecimal minPrice;
    //最高价格
    private BigDecimal maxPrice;
    //分类名称
    private List<String> categoryName;
    // 在 ProductVO 中添加
    public static List<ProductVO> fromEntities(List<Product> products) {
        return products.stream()
                .map(p -> ProductVO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .image(p.getImage())
                        .video(p.getVideo())
                        .brand(p.getBrand())
                        .placeOfOrigin(p.getPlaceOfOrigin())
                        .level(p.getLevel())
                        .offlineTime(p.getOfflineTime())
                        .publishTime(p.getPublishTime())
                        .creator(p.getCreator())
                        .updater(p.getUpdater())
                        .createTime(p.getCreateTime())
                        .updateTime(p.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
    }
}
