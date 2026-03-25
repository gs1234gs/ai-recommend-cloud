package com.guanshiyun.address;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@ToString(callSuper = true)
@Table("order_address")
public class OrderAddress extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //地址id
    @Id
    private Long id;
    //地址
    private String address;
    //联系电话
    private String phone;
    //联系人姓名
    private String name;
    //描述
    private String description;
}
