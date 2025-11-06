package com.guanshiyun.profile;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("search_content")
public class SearchContent extends BasePojo {
    //搜索内容id
    @Id
    private BigInteger id;
    //搜索内容
    private String content;
    //浏览时长，秒
    private Integer duration;
//     价格区间,
    //最高价格
    private BigDecimal maxPrice;
    //最低价格
    private BigDecimal minPrice;
    //、品牌等
    private BigInteger brandId;
    /**商品类别 */
    private Integer goodsCategoryId;
}
