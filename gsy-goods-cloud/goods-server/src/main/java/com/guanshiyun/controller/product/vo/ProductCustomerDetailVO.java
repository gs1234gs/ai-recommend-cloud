package com.guanshiyun.controller.product.vo;

import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductCustomerDetailVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //商品id
    private Long id;
    //商品名称
    private String name;
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
    private BigDecimal discountPrice;
    private BigDecimal originalPrice;
    /**
     * 上架时间
     */
    private LocalDateTime publishTime;
    /**
     * 下架时间
     */
    private LocalDateTime offlineTime;
    //最低价格
    private BigDecimal minPrice;
    //最高价格
    private BigDecimal maxPrice;
    //sku列表
    private List<SKUVO> skuList;
    //标签列表
    private List<TagVO> tagList;
}
