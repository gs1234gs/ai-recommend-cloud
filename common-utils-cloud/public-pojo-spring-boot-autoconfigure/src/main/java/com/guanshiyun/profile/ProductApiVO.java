package com.guanshiyun.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductApiVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //商品id
    private Long id;
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
    //商品状态，1=下架，0=上架
    private short status;
    private LocalDateTime publishTime; // 上架时间
    private LocalDateTime offlineTime; // 下架时间
    //创建开始时间
    private LocalDateTime startTime;
    //创建结束时间
    private LocalDateTime endTime;
}
