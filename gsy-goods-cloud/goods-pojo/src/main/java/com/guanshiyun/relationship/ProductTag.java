package com.guanshiyun.relationship;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;



/**
 *
 * 商品-标签中间表
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("product_tag")
public class ProductTag extends BasePojo {
    /** 主键id */
    @Id
    private Long id;
    /** 商品id */
    private Long productId;
    /** 标签id */
    private Long tagId;
    //租户id
    private Long tenantId;
}
