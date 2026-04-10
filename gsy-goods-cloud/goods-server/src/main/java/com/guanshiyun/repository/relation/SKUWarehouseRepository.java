package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.SKUWarehouse;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface SKUWarehouseRepository extends R2dbcRepository<SKUWarehouse, Long> {
    Flux<Long> findSkuIdByWarehouseId(Long warehouseId);
    Mono<Void> deleteAllBySkuId( Long skuId);
    Flux<SKUWarehouse> findBySkuId(Long skuId);
}
