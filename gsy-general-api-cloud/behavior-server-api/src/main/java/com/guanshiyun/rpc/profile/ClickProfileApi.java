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


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Accessors(chain = true)
public class ClickProfileApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //用户点击画像
    private BigInteger id;
    //点击时间
    private LocalDateTime clickTime;
    //点击skuId
    private ProductApiVO product;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;
}
