package com.guanshiyun.repository.sku;

import com.guanshiyun.sku.SKU;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface SKURepository extends ReactiveCrudRepository<SKU, Long> {
    @Query("DELETE FROM sku WHERE product_id = :productId")
    Mono<Void> deleteAllByProductId(@Param("productId") Long productId);

    //返回加个最低的，相同也只返回一个
    @Query("""
            SELECT * FROM sku 
            WHERE product_id = :productId 
              AND price = (
                SELECT MIN(price) FROM sku WHERE product_id = :productId
              )
            LIMIT 1
            """)
    Mono<SKU> findSKUIDByProductId(Long productId);

    //根据商品id统计销量
    @Query("""
            SELECT COALESCE(SUM(sales_volume), 0) 
            FROM sku 
            WHERE product_id = :productId
            """)
    Mono<Integer> sumSalesByProductId(Long productId);

    @Query("""
            SELECT * FROM sku WHERE product_id = :productId
            """)
    Flux<SKU> findAllByProductId(Long productId);

    //根据id减库存
    @Query("UPDATE product SET stock = stock - :count WHERE id = :id AND stock >= :count")
    Mono<Integer> reduceStockById(@Param("id") Long id, @Param("count") Integer count);

    //添加库存
    @Query("UPDATE product SET stock = stock + :count WHERE id = :id")
    Mono<Integer> addStockById(@Param("id") Long id, @Param("count") Integer count);

    @Query("""
            SELECT * FROM sku WHERE product_id IN (:allProductIds)
            """)
    Flux<SKU> findAllByProductId(List<Long> allProductIds);

    @Query("""
            SELECT product_id 
                  FROM sku 
                  GROUP BY product_id 
                  HAVING SUM(sales_volume) > :salesVolume
            """)
    Flux<Long> findProductIdsByTotalSalesGreaterThan(@Param("salesVolume") Integer salesVolume);

    // 4. 库存预警列表 (必须保留)
    @Query("""
            SELECT * FROM sku 
            WHERE tenant_id = :tenantId 
            AND stock < :threshold
            ORDER BY stock ASC
            LIMIT :limit
            """)
    Flux<SKU> getLowStockSKUs(
            @Param("tenantId") Long tenantId,
            @Param("threshold") Integer threshold,
            @Param("limit") Integer limit);

    // 5. 热销排行 (必须保留)
    @Query("""
            SELECT * FROM sku 
            WHERE tenant_id = :tenantId 
            ORDER BY sales_volume DESC
            LIMIT :limit
            """)
    Flux<SKU> getTopSellingSKUs(
            @Param("tenantId") Long tenantId,
            @Param("limit") Integer limit);

    // 6. 滞销排行 (必须保留)
    @Query("""
            SELECT * FROM sku 
            WHERE tenant_id = :tenantId 
            AND create_time >= :startTime 
            ORDER BY sales_volume ASC
            LIMIT :limit
            """)
    Flux<SKU> getSlowMovingSKUs(
            @Param("tenantId") Long tenantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("limit") Integer limit);

    @Query("SELECT tenant_id FROM sku WHERE id = :id")
    Mono<Long> findTenantIdById(Long id);
}
