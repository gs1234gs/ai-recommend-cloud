package com.guanshiyun.click;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@ToString
@EqualsAndHashCode(callSuper = true)
@Table("user_click")
public class UserClick extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID ,唯一标识*/
    @Id
    private BigInteger id;

}
