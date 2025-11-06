package com.guanshiyun.order;

import com.guanshiyun.base.BasePojo;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

//订单
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@ToString(callSuper = true)
@Table("purchase_order")
public class PurChaseOrder extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //订单id
    @Id
    private BigInteger id;
    //订单编号
    private String orderNo;
    //订单状态，1-待付款，2-待发货，3-待收货，4-待评价，5-已完成，6-已取消
    private Integer status;
    //付款状态，1-未付款，2-已付款
    private Integer payStatus;
    //收货状态，1-未收货，2-已收货
    private Integer receiveStatus;
    //订单金额
    private BigDecimal amount;
    //支付时间
    private LocalDateTime payTime;
    //下单时间
    private LocalDateTime orderPlacementTime;
    //商品id
    private BigInteger skuId;
    //付款方式，1-在线支付，2-货到付款，3-其他
    private String payType;
    //配送费用
    private BigInteger deliveryFee;
    //配送方式
    private String delivery;
    //实付金额
    private BigInteger payAmount;
    //订单备注
    private String remark;
    //商品数量
    private Integer num;
}
