package com.guanshiyun.orderItem;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

//订单明细
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("order_item")
public class OrderItem extends BasePojo {
    //订单明细id
    private BigInteger id;
    //订单id
    private BigInteger orderId;
    //商品id
    private BigInteger goodsId;
    //商品数量
    private Integer num;
}
