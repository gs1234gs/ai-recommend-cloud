package com.guanshiyun.relationship;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

/**
 *
 * 商品-标签中间表
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("product_tag")
public class ProductTag extends BasePojo {
    /** 主键id */
    @Id
    private BigInteger id;
    /** 商品id */
    private BigInteger productId;
    /** 标签id */
    private BigInteger tagId;
}
