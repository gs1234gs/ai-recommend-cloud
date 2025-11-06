package com.guanshiyun.orderItem;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

//订单明细
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@ToString(callSuper = true)
@Table("order_item")
public class OrderItem extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //订单明细id
    @Id
    private BigInteger id;
    //订单id
    private BigInteger purchaseOrderId;
    //收货地址
    private BigInteger addressId;
}
