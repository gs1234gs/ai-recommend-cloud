package com.guanshiyun.relationship;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

/**
 * 产品仓库关系
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("product_warehouse")
public class ProductWarehouse extends BasePojo {
    /** 主键ID */
    @Id
    private BigInteger id;
    /** 产品ID */
    private BigInteger productId;
    /** 仓库ID */
    private BigInteger warehouseId;
}
