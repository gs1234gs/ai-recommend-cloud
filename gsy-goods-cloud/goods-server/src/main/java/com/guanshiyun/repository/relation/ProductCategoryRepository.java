package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductCategory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.List;

public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, BigInteger> {

    @Query("select * from product_category where product_id = :productId")
    Flux<ProductCategory> findByProductId(BigInteger productId);

    @Query("select * from product_category where product_id in (:productList)")
    Flux<ProductCategory> findByProductIdIn(List<BigInteger> productList);

    @Query("select * from product_category where category_id = :categoryId")
    Flux<ProductCategory> findByCategoryId(BigInteger categoryId);
}
