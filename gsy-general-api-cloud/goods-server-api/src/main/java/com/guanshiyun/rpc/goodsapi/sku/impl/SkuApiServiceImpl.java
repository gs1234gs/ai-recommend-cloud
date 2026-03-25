package com.guanshiyun.rpc.goodsapi.sku.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.publicvo.SKUGroupByProductIdApiVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.profile.SKUApiVO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuApiServiceImpl implements SkuApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    //根据商品id获取SKU列表
    @Override
    public Mono<ResultT<List<SKUApiVO>>> findByProductId(Long productId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.SKU_FIND_PRODUCT)
                        .build(productId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<SKUApiVO>>>() {});
    }

    //分页查询SKU列表按商品分组
    @Override
    public Mono<ResultT<List<SKUGroupByProductIdApiVO>>> findByPage(RequestPage<SKUApiVO> requestPage) {
        return goodsWebClientRpc
                .webClient()
                .post()
                .uri(builder -> builder
                        .path(GoodsApiUrl.SKU_FIND_PAGE)
                        .build()
                )
                .bodyValue(requestPage)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<SKUGroupByProductIdApiVO>>>() {});
    }

    @Override
    public Mono<ResultT<List<SKUApiVO>>> findBySkuIds(List<Long> skuIds) {
        return goodsWebClientRpc.webClient()
                .get()
                .uri(builder-> builder.path(GoodsApiUrl.SKU_FIND_BY_SKU_IDS)
                        .queryParam("skuIds",skuIds)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<SKUApiVO>>>() {
                });
    }

    @Override
    public Mono<ResultT<SKUApiVO>> findBySkuId(Long skuId) {
        return goodsWebClientRpc.webClient()
                .get()
                .uri(builder-> builder.path(GoodsApiUrl.SKU_FIND_BY_ID)
                        .build(skuId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<SKUApiVO>>() {
                });
    }

    @Override
    public Mono<ResultT<Boolean>> reduceStockAndAddSales(Long skuId, Integer count) {
        return goodsWebClientRpc.webClient()
                .put()
                .uri(builder-> builder.path(GoodsApiUrl.SKU_ADD_SALES_BY_ID)
                        .queryParam("count",count)
                        .queryParam("id",skuId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<Boolean>>() {
                });
    }
}
