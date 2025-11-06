package com.guanshiyun.rpc.address.impl;

import com.guanshiyun.aienums.OrderPrefix;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.address.OrderAddressServiceApi;
import com.guanshiyun.rpc.address.vo.OrderAddressVOApi;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
public class OrderAddressServiceApiImpl implements OrderAddressServiceApi {
    private final WebClient.Builder webClientBuilder;
    public OrderAddressServiceApiImpl ( WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder.baseUrl(OrderPrefix.BASE_URL);
    }

    /**
     * 根据订单id，或者订单号，获取订单配送地址信息
     * */
    @Override
    public Mono<ResultT<OrderAddressVOApi>> findOrderAddress(BigInteger orderId) {
        return null;
    }
}
