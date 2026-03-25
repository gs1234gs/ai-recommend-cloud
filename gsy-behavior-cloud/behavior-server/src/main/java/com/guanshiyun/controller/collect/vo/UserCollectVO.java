package com.guanshiyun.controller.collect.vo;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Accessors(chain = true)
public class UserCollectVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //id
    private Long id;
    //商品id
    private ProductApiVO product;
    //收藏时间
    private LocalDateTime collectTime;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;
}
