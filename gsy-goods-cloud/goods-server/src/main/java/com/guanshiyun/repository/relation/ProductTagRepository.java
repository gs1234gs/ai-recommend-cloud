package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductTag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface ProductTagRepository extends ReactiveCrudRepository<ProductTag, BigInteger> {
    @Query("select product_id from product_tag where tag_id = :tagId")
    Flux<BigInteger> findByTagId(@Param("tagId")BigInteger tagId);

    @Query("delete from product_tag where product_id = :productId")
     Mono<Void> deleteAllByProductId(@Param("productId") BigInteger productId);

    @Query("select tag_id from product_tag where product_id = :productId")
    Flux<BigInteger> findTagIdByProductId(BigInteger productId);

   @Query("select product_id from product_tag where tag_id in (:tagIds)")
    Flux<BigInteger> findByTagIds(List<BigInteger> tagIds);
   //根据商品tagIds删除
    @Query("delete from product_tag where tag_id in (:tagIds)")
    Mono<Void> deleteAllByTagIds(@Param("tagIds") List<BigInteger> tagIds);
}
