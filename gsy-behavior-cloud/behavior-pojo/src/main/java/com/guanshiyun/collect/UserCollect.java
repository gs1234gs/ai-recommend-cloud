package com.guanshiyun.collect;

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
 * 收藏记录
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Table("user_collect")
public class UserCollect extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id，即会话id
    @Id
    private BigInteger id;
}
