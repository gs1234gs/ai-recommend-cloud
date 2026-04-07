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

import java.io.Serial;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("product_category")
public class ProductCategory extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;
    @Id
    private Long id;
    private Long productId;
    private Long categoryId;
    //租户id
    private Long tenantId;
}
