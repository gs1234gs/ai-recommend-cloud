package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductCategory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, Long> {

    @Query("select * from product_category where product_id = :productId")
    Flux<ProductCategory> findByProductId(Long productId);

    @Query("select * from product_category where product_id in (:productList)")
    Flux<ProductCategory> findByProductIdIn(List<Long> productList);

    @Query("select * from product_category where category_id = :categoryId")
    Flux<ProductCategory> findByCategoryId(Long categoryId);

    @Query("select * from product_category where product_id in (:productList)")
    Mono<Void> deleteAllByProductIds(List<Long> productList);

    @Query("delete from product_category where product_id = :productId")
    Mono<Void> deleteByProductId(Long productId);
}
