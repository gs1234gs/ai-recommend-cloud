package com.guanshiyun.rpc.order.impl;

import com.guanshiyun.aienums.OrderApiUrlEnum;
import com.guanshiyun.aienums.OrderParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.OrderWebClientRpc;
import com.guanshiyun.rpc.order.PurchaseOrderServiceApi;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceApiImpl implements PurchaseOrderServiceApi {
    private final OrderWebClientRpc webClientRpc;
    /**
     *
     * 获取订单列表
     * */
    @Override
    public Mono<ResultT<List<PurchaseOrderVOApi>>> findByUserId(BigInteger userId) {
        return webClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(OrderApiUrlEnum.ORDER_FIND_BY_USER_ID.getUrl())
                                .queryParam(OrderParamKey.USER_ID,   userId)// 参数
                                .build())
                .header(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<PurchaseOrderVOApi>>>() {});
    }

    /**
     *
     * 获取指定条数订单
     * */
    @Override
    public Mono<ResultT<List<PurchaseOrderVOApi>>> findByRows(Integer row) {
        return webClientRpc
                .webClient()
                .get()
                .uri(builder->builder.path(OrderApiUrlEnum.ORDER_FIND_BY_ROWS.getUrl())
                        .queryParam(OrderParamKey.ROWS, row)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<PurchaseOrderVOApi>>>() {
                });
    }
}
