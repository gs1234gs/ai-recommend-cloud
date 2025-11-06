package com.guanshiyun.search;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("user_search")
public class UserSearch extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 会话id,唯一标识 */
    @Id
    private BigInteger id;
}
