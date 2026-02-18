package com.guanshiyun.rpc.goodsapi.product.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.profile.ProductApiVO;
import com.guanshiyun.rpc.profile.ProductCustomerApiVO;
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
public class ProductApiServiceImpl implements ProductApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    @Override
    public Mono<ResultT<CursorPageResult<List<ProductApiVO>>>> findCursor(RequestCursorPage<ProductApiVO> requestCursorPage) {
        return  goodsWebClientRpc.webClient()
                                .post()
                                .uri(builder -> builder
                                        .path(GoodsApiUrl.PRODUCT_FIND_CURSOR)
                                        .build()
                                )
                                .bodyValue(requestCursorPage)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ResultT<CursorPageResult<List<ProductApiVO>>>>() {
                                });
    }

    @Override
    public Mono<ResultT<ProductApiVO>> findProductById(BigInteger id) {
        return goodsWebClientRpc.webClient()
                .post()
                .uri(builder -> builder
                        .path(GoodsApiUrl.PRODUCT_FIND_BY_ID)
                        .build(id)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<ProductApiVO>>() {});
    }

    @Override///recommendByIds
    public Mono<ResultT<List<ProductCustomerApiVO>>> findProductsByIds(List<BigInteger> ids) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder->builder.path(GoodsApiUrl.RECOMMEND_PRODUCT_FIND_BY_IDS)
                        .queryParam("ids", ids)
                        .build())
                .header(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY,ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<ProductCustomerApiVO>>>() {});
    }
}
