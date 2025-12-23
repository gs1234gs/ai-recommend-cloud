package com.guanshiyun.repository.order;

import com.guanshiyun.order.PurChaseOrder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface PurChaseOrderRepository extends ReactiveCrudRepository<PurChaseOrder, BigInteger> {

    //根据用户id查询，查询rows订单

    @Query("SELECT * FROM pur_chase_order WHERE creator = :userId ORDER BY id DESC LIMIT :rows")
    Flux<PurChaseOrder> findAllByUserId(@Param("userId") BigInteger userId, @Param("rows") Integer rows);

    @Query("SELECT * FROM pur_chase_order WHERE creator IN (:userIds) ORDER BY id DESC LIMIT :rows")
    Flux<PurChaseOrder> findAllByUserIds(
            @Param("userIds") List<BigInteger> userIds,
            @Param("rows") Integer rows);

    @Query("SELECT address_id FROM pur_chase_order WHERE id IN (:orderIds)")
    Flux<BigInteger> findAllAddressById(Collection<BigInteger> orderIds);
}
