package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductCategory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, BigInteger> {

    @Query("select * from product_category where product_id = :productId")
    Flux<ProductCategory> findByProductId(BigInteger productId);
}
