package com.guanshiyun.publicbehaviorvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductApiVO {
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
    //创建开始时间
    private LocalDateTime startTime;
    //创建结束时间
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private BigInteger creator;
    private BigInteger updater;
}
