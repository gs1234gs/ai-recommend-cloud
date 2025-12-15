package com.guanshiyun.rpc.goodsapi.sku.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.profile.SKUApiVO;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuApiServiceImpl implements SkuApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    //根据商品id获取SKU列表
    @Override
    public Mono<ResultT<List<SKUApiVO>>> findByProductId(BigInteger productId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.SKU_FIND_PRODUCT)
                        .build(productId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<SKUApiVO>>>() {});
    }

    //分页查询SKU列表
    @Override
    public Mono<ResultT<List<SKUApiVO>>> findByPage(RequestPage<SKUApiVO> requestPage) {
        return goodsWebClientRpc
                .webClient()
                .post()
                .uri(builder -> builder
                        .path(GoodsApiUrl.SKU_FIND_PAGE)
                        .build()
                )
                .bodyValue(requestPage)
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }
}
