package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.SKUWarehouse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface SKUWarehouseRepository extends ReactiveCrudRepository<SKUWarehouse, BigInteger> {
    @Query("select product_id from product_warehouse where warehouse_id = :warehouseId")
    Flux<BigInteger> findByWarehouseId(BigInteger warehouseId);

    @Query("delete from product_warehouse where product_id = :id")
    Mono<Void> deleteAllByProductId(@Param("id") BigInteger id);

    @Query("select * from product_warehouse where product_id = :productId")
    Flux<SKUWarehouse> findByProductId(BigInteger productId);
}
