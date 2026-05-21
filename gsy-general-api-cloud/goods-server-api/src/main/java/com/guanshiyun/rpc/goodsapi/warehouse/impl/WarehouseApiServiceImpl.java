package com.guanshiyun.rpc.goodsapi.warehouse.impl;

import com.guanshiyun.goodsenum.GoodsApiUrl;
import com.guanshiyun.profile.WarehouseApiVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.warehouse.WarehouseApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<WarehouseApiVO>>>() {
                });
    }

    //获取仓库列表
    @Override
    public Mono<ResultT<List<WarehouseApiVO>>> findByProductId(Long productId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_BY_PRODUCT_ID)
                        .build(productId)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<WarehouseApiVO>>>() {
                });
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
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<WarehouseApiVO>>>() {
                });
    }

    @Override
    public Mono<ResultT<WarehouseApiVO>> findById(Long warehouseId) {
        return goodsWebClientRpc
                .webClient()
                .get()
                .uri(builder -> builder
                        .path(GoodsApiUrl.WAREHOUSE_FIND_BY_ID)
                        .build(warehouseId)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<WarehouseApiVO>>() {
                });
    }
}
