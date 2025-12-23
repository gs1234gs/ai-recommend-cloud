package com.guanshiyun.rpc.address.impl;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.address.OrderAddressServiceApi;
import com.guanshiyun.rpc.address.vo.OrderAddressVOApi;
import com.guanshiyun.rpc.config.OrderWebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class OrderAddressServiceApiImpl implements OrderAddressServiceApi {
    private final OrderWebClientRpc webClientRpc;

    /**
     * 根据订单id，或者订单号，获取订单配送地址信息
     * */
    @Override
    public Mono<ResultT<OrderAddressVOApi>> findOrderAddress(BigInteger orderId) {
        return webClientRpc.webClient()
                .get()
                .uri("/address/findByOrderId/{orderId}", orderId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<OrderAddressVOApi>>() {});
    }
}
