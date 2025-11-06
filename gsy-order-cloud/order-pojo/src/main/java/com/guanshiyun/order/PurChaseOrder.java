package com.guanshiyun.order;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

//订单
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("purchase_order")
public class PurChaseOrder extends BasePojo {
    //订单id
    private BigInteger id;
    //订单编号
    private String orderNo;
    //订单状态
    private short status;
    //订单金额
    private BigDecimal amount;
    //支付时间
    private LocalDateTime payTime;
    //下单时间
    private LocalDateTime orderPlacementTime;
    //收货人
    private String consignee;
    //收货地址
    private BigInteger addressId;
    //收获方式
    private short receivingMethod;
    //订单备注
    private String remark;
}
