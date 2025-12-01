package com.guanshiyun.rpc.goodsapi.product.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.profile.ProductApiVO;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApiServiceImpl implements ProductApiService {
    private final WebClientRpc webClientRpc;

    @Override
    public Mono<ResultT<List<ProductApiVO>>> findCursor(RequestCursorPage<ProductApiVO> requestCursorPage) {
        return  webClientRpc
                                .webClient()
                                .post()
                                .uri(builder -> builder
                                        .path(GoodsApiUrl.PRODUCT_FIND_CURSOR)
                                        .build()
                                )
                                .bodyValue(requestCursorPage)
                                .retrieve()
                                .bodyToMono(WebClientUtils.typeRef());
    }
}
