package com.guanshiyun.rpc.profile;


import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户画像（兴趣、偏好、地理位置等）
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@Accessors(chain = true)
public class BrowseProfileApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    private BigInteger id;
    //sku id
    private List<ProductApiVO> product;
    //浏览开始时间
    private LocalDateTime browseStartTime;
    //浏览结束时间
    private LocalDateTime browseEndTime;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;

}
