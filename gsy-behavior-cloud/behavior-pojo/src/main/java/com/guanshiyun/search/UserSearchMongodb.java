package com.guanshiyun.search;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Document("user_search")
@Accessors(chain = true)
public class UserSearchMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // id
    @Id
    private Long id;
    //搜索内容
    private String searchContent;
    // 最高价格
    private BigDecimal maxPrice;
    //最低价格
    private BigDecimal minPrice;
    //品牌id等
    private Long brandId;
    /**商品类别 */
    private Long categoryId;
    //搜索时间
    private LocalDateTime searchTime;
}
