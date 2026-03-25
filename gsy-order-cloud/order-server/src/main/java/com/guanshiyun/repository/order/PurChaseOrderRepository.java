package com.guanshiyun.repository.order;

import com.guanshiyun.order.PurChaseOrder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Collection;
import java.util.List;

public interface PurChaseOrderRepository extends ReactiveCrudRepository<PurChaseOrder, Long> {

    //根据用户id查询，查询rows订单

    @Query("SELECT * FROM purchase_order WHERE creator = :userId ORDER BY id DESC LIMIT :rows")
    Flux<PurChaseOrder> findAllByUserId(@Param("userId") Long userId, @Param("rows") Integer rows);

    @Query("SELECT * FROM purchase_order WHERE creator IN (:userIds) ORDER BY id DESC LIMIT :rows")
    Flux<PurChaseOrder> findAllByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("rows") Integer rows);

    @Query("SELECT address_id FROM purchase_order WHERE id IN (:orderIds)")
    Flux<Long> findAllAddressById(Collection<Long> orderIds);

    @Query("update purchase_order set del_flag = 1 where id = (:id)")
    Mono<Integer> softDeleteById(Long id);
}
