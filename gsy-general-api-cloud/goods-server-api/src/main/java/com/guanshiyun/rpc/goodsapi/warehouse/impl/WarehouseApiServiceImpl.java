package com.guanshiyun.rpc.goodsapi.warehouse.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.warehouse.WarehouseApiService;
import com.guanshiyun.rpc.profile.WarehouseApiVO;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseApiServiceImpl implements WarehouseApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;

    //分页查询仓库列表
    @Override
    public Mono<ResultT<List<WarehouseApiVO>>> findByPage(RequestPage<WarehouseApiVO> requestPage) {
        return goodsWebClientRpc
                .webClient()
                .post()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_CURSOR)
                        .build()
                )
                .bodyValue(requestPage)
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }

    //获取仓库列表
    @Override
    public Mono<ResultT<List<WarehouseApiVO>>> findByProductId(BigInteger productId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_BY_PRODUCT_ID)
                        .build(productId)
                )
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }

    //获取仓库列表
    @Override
    public Mono<ResultT<List<WarehouseApiVO>>> findAll() {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_ALL)
                        .build()
                )
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }

    @Override
    public Mono<ResultT<WarehouseApiVO>> findById(BigInteger warehouseId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_BY_ID)
                        .build(warehouseId)
                )
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());
    }
}
