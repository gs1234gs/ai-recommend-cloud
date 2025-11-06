package com.guanshiyun.bigmodel;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Builder
@Table("big_model")
public class BigModel extends BasePojo {
    //大模型id
    @Id
    private BigInteger id;
    //大模型名称
    private String name;
    //大模型类型
    private Integer type;
    //创建者
    private BigInteger creator;
    //更新者
    private BigInteger updater;
    //创建时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;
    //大模型状态，0-未启用，1-启用
    private short status;
    //删除标记,0-未删除，1-已删除
    private short delFlag;
    //大模型描述
    private String description;
    //大模型版本
    private String version;
    //租户id
    public BigInteger tenantId;
}
