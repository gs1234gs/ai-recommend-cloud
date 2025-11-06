package com.guanshiyun.repository.item;

import com.guanshiyun.orderItem.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;


public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, BigInteger> {


    @Query("select address_id from order_item where purchase_order_id = :orderId")
    Mono<BigInteger> findByOrderId(@Param("orderId") BigInteger orderId);

    @Query("SELECT address_id FROM order_item WHERE creator = :userId")
    Flux<BigInteger> findByUserId(@Param("userId") BigInteger userId);

    @Query("SELECT address_id FROM order_item WHERE purchase_order_id IN (:orderIds)")
    Flux<BigInteger> findByOrderIds(Collection<BigInteger> orderIds);
}
