package com.guanshiyun.rpc.goodsapi.category.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryApiServiceImpl implements CategoryApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    @Override
    public Mono<ResultT<List<CategoryApiVO>>> findAll() {
        return  goodsWebClientRpc
                                .webClient()
                                .get()
                                .uri(builder -> builder
                                        .path(GoodsApiUrl.CATEGORY_FIND_BY_ALL)
                                        .build()
                                )
                                .retrieve()
                                .bodyToMono(WebClientUtils.typeRef());
    }

    @Override
    public Mono<ResultT<List<CategoryApiVO>>> findByProductId(Long productId) {
        return  goodsWebClientRpc
                        .webClient()
                        .get()
                        .uri(builder -> builder
                                .path(GoodsApiUrl.CATEGORY_FIND_BY_PRODUCT_ID)
                                .build(productId)
                        )
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ResultT<List<CategoryApiVO>>>() {});
    }
}
