package com.guanshiyun.service.address;

import com.guanshiyun.controller.address.vo.OrderAddressSaveVO;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface OrderAddressService {
    Mono<BigInteger> save(OrderAddressSaveVO orderAddressSaveVO);
    //删除地址
    Mono<Void> deleteById(BigInteger id);

    Mono<OrderAddressVO> findByOrderId(BigInteger orderId);
    //获取批量
    Mono<List<OrderAddressVO>> findByUserId(BigInteger userId);
    //根据用户id获取地址
    Mono<List<OrderAddressVO>> findByUserId();
    //根据订单id查询地址
    Mono<List<OrderAddressVO>> findByOrderIds(Collection<BigInteger> orderIds);

    Mono<OrderAddressVO> findById(BigInteger id);
}
