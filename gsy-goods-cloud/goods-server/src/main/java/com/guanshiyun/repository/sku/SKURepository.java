package com.guanshiyun.repository;

import com.guanshiyun.sku.SKU;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface SKURepository extends ReactiveCrudRepository<SKU, BigInteger> {
    @Query("DELETE FROM sku WHERE product_id = :productId")
    Mono<Void> deleteAllByProductId(@Param("productId") BigInteger productId);

    //返回加个最低的，相同也只返回一个
    @Query("""
            SELECT * FROM sku 
            WHERE product_id = :productId 
              AND price = (
                SELECT MIN(price) FROM sku WHERE product_id = :productId
              )
            LIMIT 1
            """)
    Mono<SKU> findSKUIDByProductId(BigInteger productId);

    //根据商品id统计销量
    @Query("""
            SELECT COALESCE(SUM(sales_volume), 0) 
            FROM sku 
            WHERE product_id = :productId
            """)
    Mono<Integer> sumSalesByProductId(BigInteger productId);

    @Query("""
            SELECT * FROM sku WHERE product_id = :productId
            """)
    Flux<List<SKU>> findAllByProductId(BigInteger productId);
}
