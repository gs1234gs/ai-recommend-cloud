package com.guanshiyun.rpc.address;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.address.vo.OrderAddressVOApi;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface OrderAddressServiceApi {
    //获取订单配送地址
    Mono<ResultT<OrderAddressVOApi>> findOrderAddress(BigInteger orderId);
}
