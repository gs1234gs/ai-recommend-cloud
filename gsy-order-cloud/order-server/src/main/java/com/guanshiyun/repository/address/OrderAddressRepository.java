package com.guanshiyun.repository.address;

import com.guanshiyun.address.OrderAddress;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;


import java.util.List;

public interface OrderAddressRepository extends R2dbcRepository<OrderAddress, Long> {


    @Query("SELECT * FROM order_address WHERE id IN (:addressListId)")
    Flux<OrderAddress> findByOrderIds(@Param("addressListId") List<Long> addressListId);

    @Query("SELECT id FROM order_item WHERE user_id = :userId")
    Flux<Long> findByUserId(Long userId);
}
