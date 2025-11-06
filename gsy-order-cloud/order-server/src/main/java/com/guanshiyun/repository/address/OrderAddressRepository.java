package com.guanshiyun.repository.address;

import com.guanshiyun.address.OrderAddress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface AddressRepository extends ReactiveCrudRepository<OrderAddress, BigInteger> {
}
