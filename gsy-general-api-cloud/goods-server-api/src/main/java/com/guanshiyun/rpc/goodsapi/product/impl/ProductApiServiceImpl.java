package com.guanshiyun.rpc.goodsapi.product.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.profile.ProductApiVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
