package com.guanshiyun.rpc.goodsapi.tag.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.rpc.profile.TagApiVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class TagApiServiceImpl implements TagApiService {
    private final WebClientRpc webClientRpc;

    @Override
    public Mono<TagApiVO> findById(BigInteger id) {
        return webClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.TAG_FIND_BY_ID)
                        .build(id))
                .retrieve()
                .bodyToMono(TagApiVO.class);
    }

    @Override
    public Mono<TagApiVO> findByProductId(BigInteger productId) {
        return webClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.TAG_FIND_BY_PRODUCT_ID)
                        .build(productId))
                .retrieve()
                .bodyToMono(TagApiVO.class);
    }
}
