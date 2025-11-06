package com.guanshiyun.browse;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
/**
 * 浏览记录实体
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Table("user_browse")
public class UserBrowse extends BasePojo implements Serializable {
    /**
     * 序列化ID
     */
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    @Id
    private BigInteger id;
}
