package com.guanshiyun.rpc.goodsapi.sku;

import com.guanshiyun.publicvo.SKUGroupByProductIdApiVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.profile.SKUApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SkuApiService {
    //查询商品sku列表
    Mono<ResultT<List<SKUApiVO>>> findByProductId(BigInteger productId);
    //查询商品sku列表按商品分组
    Mono<ResultT<List<SKUGroupByProductIdApiVO>>> findByPage(RequestPage<SKUApiVO> requestPage);
    //根据id获取SKU
    Mono<ResultT<List<SKUApiVO>>> findBySkuIds(List<BigInteger> skuIds);
    //通过id获取
    Mono<ResultT<SKUApiVO>> findBySkuId(BigInteger skuId);
    //减少库存，增加销量
    Mono<ResultT<Boolean>> reduceStockAndAddSales(BigInteger skuId, Integer count);

}
