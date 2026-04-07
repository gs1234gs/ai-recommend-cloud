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


/**
 * 产品仓库关系
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("sku_warehouse")
public class SKUWarehouse extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    @Id
    private Long id;
    /** 产品ID */
    private Long skuId;
    /** 仓库ID */
    private Long warehouseId;
    //租户id
    private Long tenantId;
}
