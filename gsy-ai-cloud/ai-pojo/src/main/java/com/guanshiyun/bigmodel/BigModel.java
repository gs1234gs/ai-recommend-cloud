package com.guanshiyun.bigmodel;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 模型实体
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Builder
@Table("big_model")
@Accessors(chain = true)
public class BigModel {
    //大模型id
    @Id
    private BigInteger id;
    //大模型名称
    private String name;
    //大模型类型
    private Integer type;
    //大模型状态，0-未启用，1-启用
    private short status;
    //大模型描述
    private String description;
    //大模型版本
    private String version;
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
