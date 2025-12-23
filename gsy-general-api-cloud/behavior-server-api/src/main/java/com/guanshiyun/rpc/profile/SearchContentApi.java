package com.guanshiyun.rpc.profile;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@FieldNameConstants
public class SearchContentApi extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //搜索内容id
    private BigInteger id;
    //商品最低价格
    private BigDecimal minPrice;
    //商品最高价格
    private BigDecimal maxPrice;
    //品牌id等
    private BigInteger brandId;
    //搜索内容
    private String searchContent;
    //搜索时间
    private LocalDateTime searchTime;
}
