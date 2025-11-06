package com.guanshiyun.warehouse;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;


/**
 * 产品仓库
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("warehouse")
public class Warehouse extends BasePojo {
    /** 主键ID */
    @Id
    private BigInteger id;
    /** 仓库名称 */
    private String name;
    /** 仓库地址 */
    private String address;
    /** 仓库容量（单位：件） */
    private Integer capacity;
    /** 仓库状态（0=禁用，1=启用） */
    private short status;
    /** 仓库管理员ID */
    private BigInteger adminId;
}
