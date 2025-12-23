package com.guanshiyun.rpc.profile;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class WarehouseApiVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
