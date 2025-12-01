package com.guanshiyun.rpc.goodsapi.category;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.CategoryApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface CategoryApiService {
    //获取分类
    Mono<ResultT<List<CategoryApiVO>>> findAll();
    //根据商品id获取分类
    Mono<ResultT<List<CategoryApiVO>>> findByProductId(BigInteger productId);
}
