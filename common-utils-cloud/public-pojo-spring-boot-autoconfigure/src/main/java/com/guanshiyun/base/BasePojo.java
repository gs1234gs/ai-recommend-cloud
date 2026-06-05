package com.guanshiyun.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

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
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class BasePojo {
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    @CreatedBy
    private Long creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    @LastModifiedBy
    private Long updater;
    /**
     * 创建时间
     */
    @CreatedDate
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @LastModifiedDate
    private LocalDateTime updateTime;
    /**
     * 是否删除，删除标记,0-未删除，1-已删除
     */
    @Column("del_flag")
    private Short delFlag;
}
