package com.guanshiyun.rpc.goodsapi.sku;

import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.SKUApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SkuApiService {
    //查询商品sku列表
    Mono<ResultT<List<SKUApiVO>>> findByProductId(BigInteger productId);
    //查询商品sku列表
    Mono<ResultT<List<SKUApiVO>>> findByPage(RequestPage<SKUApiVO> requestPage);
}
