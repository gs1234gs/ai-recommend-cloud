package com.guanshiyun.service.sku;

import com.guanshiyun.controller.sku.vo.*;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface SKUService {
    //添加SKU
    Mono<Long> save(SKUSaveVO skuVO);
//删除SKU
    Mono<Void> deleteById(Long id);
//查询SKU
    Mono<SKUVO> findById(Long id);
//分页查询SKU
Mono<PageResultT<List<SKUGroupByProductIdVO>>> findAllByPage(RequestPage<SKUFindVO> requestPage);

//Mono<PageResultT<List<Map<Long,SKUVO>>>> findAllPage(RequestPage<ProductSearchVO> requestPage);
//根据商品id获取SKU列表
    Flux<SKUVO> findByProductId(Long productId);
//批量删除
    Mono<Void> deleteAllById(List<Long> ids);

    //根据id减库存
    Mono<Boolean> reduceStockById(Long id, Integer count);

    //根据id加库存
    Mono<Boolean> addStockById(Long id, Integer count);

    Mono<List<SKUVO>> findAllByIds(List<Long> skuIds);

    Mono<Boolean> addSalesById(Long id, Integer count);

    //统计总销售与营收
    Mono<SkuStatisticsVO> totalStatistics();

}
