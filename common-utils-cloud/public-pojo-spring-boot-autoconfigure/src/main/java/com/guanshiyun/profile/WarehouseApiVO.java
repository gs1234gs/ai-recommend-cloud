package com.guanshiyun.profile;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class WarehouseApiVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    /** 仓库名称 */
    private String name;
    /** 仓库地址 */
    private String address;
    /** 仓库容量（单位：件） */
    private Integer capacity;
    /** 状态,是否禁用（0=启用，1=禁用） */
    private short status;
    /** 仓库管理员ID */
    private Long adminId;
}
