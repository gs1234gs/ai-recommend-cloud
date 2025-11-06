package com.guanshiyun.repository.warehouse;

import com.guanshiyun.warehouse.Warehouse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface WarehouseRepository extends ReactiveCrudRepository<Warehouse, BigInteger> {
}
