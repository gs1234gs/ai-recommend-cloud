package com.guanshiyun.repository.relation;

import com.guanshiyun.relationship.ProductTag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductTagRepository extends R2dbcRepository<ProductTag, Long> {
    @Query("select product_id from product_tag where tag_id = :tagId")
    Flux<Long> findByTagId(@Param("tagId")Long tagId);

    @Query("delete from product_tag where product_id = :productId")
     Mono<Void> deleteAllByProductId(@Param("productId") Long productId);

    @Query("select tag_id from product_tag where product_id = :productId")
    Flux<Long> findTagIdByProductId(Long productId);

   @Query("select product_id from product_tag where tag_id in (:tagIds)")
    Flux<Long> findByTagIds(List<Long> tagIds);
   //根据商品tagIds删除
    @Query("delete from product_tag where tag_id in (:tagIds)")
    Mono<Void> deleteAllByTagIds(@Param("tagIds") List<Long> tagIds);
}
