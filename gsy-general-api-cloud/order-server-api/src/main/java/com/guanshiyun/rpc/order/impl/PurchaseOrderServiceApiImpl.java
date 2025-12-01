package com.guanshiyun.rpc.order.impl;

import com.guanshiyun.aienums.OrderApiUrl;
import com.guanshiyun.aienums.OrderParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.rpc.order.PurchaseOrderServiceApi;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceApiImpl implements PurchaseOrderServiceApi {
    private final WebClientRpc webClientRpc;
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
                                .path(OrderApiUrl.BROWSE_FIND_BY_ROWS)
                                .queryParam(OrderParamKey.USER_ID,   userId)// 参数2
                                .build())
                .header(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY)
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }
}
