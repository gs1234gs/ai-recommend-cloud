package com.guanshiyun.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;
/**
 * 基础实体对象
 *
 * 为什么实现 {@link BasePojo} 接口？
 * 因为使用 Easy-Trans TransType.SIMPLE 模式，集成 MyBatis Plus 查询
 *
 * @author
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@Accessors(chain = true)
public class BasePojo {
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    public BigInteger creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    public BigInteger updater;
    /**
     * 创建时间
     */
    public LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    public LocalDateTime updateTime;
    /**
     * 是否删除，删除标记,0-未删除，1-已删除
     */
    public short delFlag;
}
