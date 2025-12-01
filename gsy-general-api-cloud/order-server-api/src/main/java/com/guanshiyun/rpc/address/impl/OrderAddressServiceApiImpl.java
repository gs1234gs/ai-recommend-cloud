package com.guanshiyun.rpc.address.impl;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.address.OrderAddressServiceApi;
import com.guanshiyun.rpc.address.vo.OrderAddressVOApi;
import com.guanshiyun.rpc.config.WebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class OrderAddressServiceApiImpl implements OrderAddressServiceApi {
    private final WebClientRpc webClientRpc;

    /**
     * 根据订单id，或者订单号，获取订单配送地址信息
     * */
    @Override
    public Mono<ResultT<OrderAddressVOApi>> findOrderAddress(BigInteger orderId) {
        return null;
    }
}
