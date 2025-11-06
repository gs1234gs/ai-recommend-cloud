package com.guanshiyun.rpc.order.impl;

import com.guanshiyun.aienums.OrderPrefix;
import com.guanshiyun.rpc.order.PurchaseOrderServiceApi;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.responsepojo.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@Service
public class PurchaseOrderServiceApiImpl implements PurchaseOrderServiceApi {
    private final WebClient.Builder webClientBuilder;
    public PurchaseOrderServiceApiImpl ( WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder.baseUrl(OrderPrefix.BASE_URL);
    }
    /**
     *
     * 获取订单列表
     * */
    @Override
    public Mono<ResultT<List<PurchaseOrderVOApi>>> findByUserId(BigInteger userId) {
        return null;
    }
}
