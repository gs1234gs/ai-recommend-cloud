package com.guanshiyun.rpc.goodsapi.tag.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.rpc.profile.TagApiVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TagApiServiceImpl implements TagApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    @Override
    public Mono<ResultT<TagApiVO>> findById(BigInteger id) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.TAG_FIND_BY_ID)
                        .build(id))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<TagApiVO>>() {});
    }

    @Override
    public Mono<ResultT<List<TagApiVO>>> findByProductId(BigInteger productId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> {
                    URI uri = builder
                                    .path(GoodsApiUrl.TAG_FIND_BY_PRODUCT_ID)
                                    .build(productId);
                    return uri;
                        }
                )

                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<TagApiVO>>>() {});
    }

    @Override
    public Mono<ResultT<List<TagApiVO>>> findByProductId(List<BigInteger> productIds) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> {
                            URI uri = builder
                                    .path(GoodsApiUrl.TAG_FIND_BY_PRODUCT_IDS)
                                    .queryParam("productIds", productIds)
                                    .build();
                            return uri;
                        }
                )

                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<TagApiVO>>>() {});
    }
}
