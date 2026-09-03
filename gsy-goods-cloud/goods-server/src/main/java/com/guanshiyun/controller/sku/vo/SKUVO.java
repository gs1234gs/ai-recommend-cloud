package com.guanshiyun.controller.sku.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SKUVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //商品id

    /** 名称*/
    private String name;

    /** 商品编码（内部唯一编码） */
    private String skuCode;

    /** 销售价 */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal price;

    /** 成本价（可选） */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal costPrice;

    /** 库存数量 */
    private Integer stock;
    /**要添加的库存*/
    private Integer addStock;
    /** 销售量 */
    private Integer salesVolume;

    /** 状态（1=下架，0=上架） */
    private short status;

    /** 权重（用于推荐、排序） */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal weight;
    //排序值
    private Integer sort;
    // 上架时间
    private LocalDateTime publishTime;
    // 下架时间
    private LocalDateTime offlineTime;
    // 商品详情
    private String detailContent;
    //图片
    private List<String> picList;
    //商品id
    private Long productId;
    //仓库
    private List<WarehouseVO> warehouseList;

    @SneakyThrows
    public static SKUVO toSKUVO(SKU sku) {
        return SKUVO.builder()
                .id(sku.getId())
                .name(sku.getName())
                .skuCode(sku.getSkuCode())
                .price(sku.getPrice())
                .stock(sku.getStock())
                .salesVolume(sku.getSalesVolume())
                .status(sku.getStatus())
                .weight(sku.getWeight())
                .sort(sku.getSort())
                .publishTime(sku.getPublishTime())
                .offlineTime(sku.getOfflineTime())
                .detailContent(sku.getDetailContent())
                .picList(BeanConvertUtil.parsePicList(sku.getPicList()))
                .productId(sku.getProductId())
                .build();
    }

    public static List<SKUVO> toSKUVOList(List<SKU> skus) {
       return skus.stream().map(SKUVO::toSKUVO).toList();
    }
}
