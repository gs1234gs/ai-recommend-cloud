package com.guanshiyun.repository.address;

import com.guanshiyun.address.OrderAddress;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.List;

public interface OrderAddressRepository extends ReactiveCrudRepository<OrderAddress, BigInteger> {


    @Query("SELECT * FROM order_address WHERE id IN (:addressListId)")
    Flux<OrderAddress> findByOrderIds(@Param("addressListId") List<BigInteger> addressListId);
}
