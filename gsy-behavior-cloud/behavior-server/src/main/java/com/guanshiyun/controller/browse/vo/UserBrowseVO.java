package com.guanshiyun.controller.browse.vo;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import lombok.*;
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
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserBrowseVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    private Long id;
    //商品id
    private ProductApiVO product;
    //浏览开始时间
    private LocalDateTime browseStartTime;
    //浏览结束时间
    private LocalDateTime browseEndTime;
    //ip地址
    private String ipAddress;
    //设备类型
    private String deviceType;
    //浏览时长,单位毫秒
    private Long browseDuration;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;
}
