package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.SKUWarehouse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface SKUWarehouseRepository extends ReactiveCrudRepository<SKUWarehouse, Long> {
    @Query("select product_id from product_warehouse where warehouse_id = :warehouseId")
    Flux<Long> findByWarehouseId(Long warehouseId);

    @Query("delete from product_warehouse where product_id = :id")
    Mono<Void> deleteAllByProductId(@Param("id") Long id);

    @Query("select * from product_warehouse where product_id = :productId")
    Flux<SKUWarehouse> findByProductId(Long productId);
}
