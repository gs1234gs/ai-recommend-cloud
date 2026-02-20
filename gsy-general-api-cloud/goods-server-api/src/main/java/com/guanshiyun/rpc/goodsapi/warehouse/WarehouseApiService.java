package com.guanshiyun.rpc.goodsapi.warehouse;

import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.profile.WarehouseApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface WarehouseApiService {
    Mono<ResultT<List<WarehouseApiVO>>> findByPage( RequestPage<WarehouseApiVO> requestPage);
    //根据产品id获取仓库列表
    Mono<ResultT<List<WarehouseApiVO>>> findByProductId(BigInteger productId);
    //获取仓库列表
    Mono<ResultT<List<WarehouseApiVO>>> findAll();
    //根据id获取仓库
    Mono<ResultT<WarehouseApiVO>> findById(BigInteger warehouseId);
}
