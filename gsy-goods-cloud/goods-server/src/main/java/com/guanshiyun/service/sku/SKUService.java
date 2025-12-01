package com.guanshiyun.service.sku;

import com.guanshiyun.controller.sku.vo.SKUFindVO;
import com.guanshiyun.controller.sku.vo.SKUSaveVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SKUService {
    //添加SKU
    Mono<BigInteger> save(SKUSaveVO skuVO);
//删除SKU
    Mono<Void> deleteById(BigInteger id);
//查询SKU
    Mono<SKUFindVO> findById(BigInteger id);
//分页查询SKU
        Mono<PageResultT<List<SKUFindVO>>> findAllByPage(RequestPage<SKUFindVO> requestPage);
//根据商品id获取SKU列表
    Flux<SKUVO> findByProductId(BigInteger productId);
//批量删除
    Mono<Void> deleteAllById(List<BigInteger> ids);

    //根据id减库存
    Mono<Boolean> reduceStockById(BigInteger id, Integer count);

    //根据id加库存
    Mono<Boolean> addStockById(BigInteger id, Integer count);
}
