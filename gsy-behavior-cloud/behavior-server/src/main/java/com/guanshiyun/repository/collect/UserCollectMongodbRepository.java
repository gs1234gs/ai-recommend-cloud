package com.guanshiyun.repository.collect;

import com.guanshiyun.collect.UserCollectMongodb;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface UserCollectMongodbRepository extends ReactiveMongoRepository<UserCollectMongodb, Long> {
    @Query("{ 'product.id': ?0, 'creator': ?1 }")
    Flux<UserCollectMongodb> findByProductIdAndCreator(Long productId, Long creatorId);


    /**
     * 分页查询列表
     * 关键点：
     * 1. 参数必须包含 Pageable pageable
     * 2. @Query 只需写过滤条件，不要写分页逻辑
     * 3. 返回类型可以是 Flux (流式) 或 Mono<List> (配合 collectList)
     */
    @Query("{ 'creator': ?0 }") // 假设数据库字段名是 'creator'，对应参数 creatorId
    Flux<UserCollectMongodb> findByCreatorId(Long creatorId, Pageable pageable);

    /**
     * 统计总数
     * 关键点：
     * 1. 方法名必须以 count 开头
     * 2. 参数必须和查询方法的过滤条件一致 (这里是 creatorId)
     * 3. 不需要 Pageable 参数
     */
    Mono<Long> countByCreator(Long creatorId);
    // 或者如果不写 @Query，直接利用命名推导: countByCreatorId
}
