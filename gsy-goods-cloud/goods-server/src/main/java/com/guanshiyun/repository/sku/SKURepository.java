package com.guanshiyun.repository.sku;

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
    Flux<SKU> findAllByProductId(BigInteger productId);
//根据id减库存
    @Query("UPDATE product SET stock = stock - :count WHERE id = :id AND stock >= :count")
    Mono<Integer> reduceStockById(@Param("id") BigInteger id,@Param("count") Integer count);

    //添加库存
    @Query("UPDATE product SET stock = stock + :count WHERE id = :id")
    Mono<Integer> addStockById( @Param("id")BigInteger id,@Param("count") Integer count);

    @Query("""
            SELECT * FROM sku WHERE product_id IN (:allProductIds)
            """)
    Flux<SKU> findAllByProductId(List<BigInteger> allProductIds);
}
