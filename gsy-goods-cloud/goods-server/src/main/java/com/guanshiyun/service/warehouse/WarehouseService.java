package com.guanshiyun.service.warehouse;

import com.guanshiyun.controller.warehouse.vo.WarehouseSaveVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.warehouse.Warehouse;
import reactor.core.publisher.Mono;


import java.util.Collection;
import java.util.List;

public interface WarehouseService {
    Mono<Long> save(WarehouseSaveVO warehouseSaveVO);

    Mono<Long> deleteById(Long id);

    Mono< Long> deleteAllById(Collection<Long> ids);

    Mono<PageResultT<List<WarehouseVO>>> findPage(RequestPage<WarehouseVO> requestPage);
    //批量添加，批量更新
    Mono<Long> saveAll(List<Warehouse> warehouseList);

    Mono<WarehouseVO> findById(Long id);

    Mono<List<WarehouseVO>> findAll();

}
