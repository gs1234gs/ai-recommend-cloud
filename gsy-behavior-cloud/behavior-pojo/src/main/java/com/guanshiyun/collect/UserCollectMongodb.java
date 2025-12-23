package com.guanshiyun.collect;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.rpc.profile.CategoryApiVO;
import com.guanshiyun.rpc.profile.ProductApiVO;
import com.guanshiyun.rpc.profile.SKUApiVO;
import com.guanshiyun.rpc.profile.TagApiVO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Accessors(chain = true)
@Document("user_collect")
public class UserCollectMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // id
    @Id
    private BigInteger id;
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
