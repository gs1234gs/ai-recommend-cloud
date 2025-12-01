package com.guanshiyun.rpc.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseApiVO {
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
