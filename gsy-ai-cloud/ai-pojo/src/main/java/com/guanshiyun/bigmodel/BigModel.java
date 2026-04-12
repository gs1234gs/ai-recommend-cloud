package com.guanshiyun.bigmodel;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * 模型实体
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
@Table("big_model")
@Accessors(chain = true)
public class BigModel extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //大模型id
    @Id
    private Long id;
    //大模型名称
    private String name;
    //大模型类型
    private Integer type;
    /** 状态,是否禁用（0=启用，1=禁用） */
    private short status;
    //大模型描述
    private String description;
    //大模型版本
    private String version;
}
