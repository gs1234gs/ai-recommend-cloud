package com.guanshiyun.rpc.profile;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.publicbehaviorvo.CategoryApiVO;
import com.guanshiyun.publicbehaviorvo.ProductApiVO;
import com.guanshiyun.publicbehaviorvo.SKUApiVO;
import com.guanshiyun.publicbehaviorvo.TagApiVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Accessors(chain = true)
public class CollectProfileApi extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    private BigInteger id;
    //商品 id
    private ProductApiVO product;
    //收集时间
    private BigInteger collectTime;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;
}
