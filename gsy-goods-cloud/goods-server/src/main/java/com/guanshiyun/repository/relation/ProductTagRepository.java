package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductTag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface ProductTagRepository extends ReactiveCrudRepository<ProductTag, BigInteger> {
    @Query("select product_id from product_tag where tag_id = :tagId")
    Flux<BigInteger> findByTagId(@Param("tagId")BigInteger tagId);

    @Query("delete from product_tag where product_id = :tagId")
     Mono<Void> deleteAllByProductId(@Param("tagId") BigInteger tagId);

    @Query("select tag_id from product_tag where product_id = :productId")
    Mono<BigInteger> findTagByProductId(BigInteger productId);
}
