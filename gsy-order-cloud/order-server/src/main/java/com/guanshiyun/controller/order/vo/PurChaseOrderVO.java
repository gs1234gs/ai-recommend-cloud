package com.guanshiyun.controller.order.vo;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.profile.TagApiVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class PurChaseOrderVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
    private BigDecimal deliveryFee;
    //配送方式
    private String delivery;
    //实付金额
    private BigDecimal payAmount;
    //订单备注
    private String remark;
    //商品数量
    private Integer num;
    //收获地址信息
    private OrderAddressVO orderAddressVO;
    //订单名称
    private String name;
    //标签
    private List<TagApiVO> tags;
    //图片
    private String image;
    //商品id
    private BigInteger productId;
}
