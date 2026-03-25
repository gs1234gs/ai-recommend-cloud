package com.guanshiyun.repository.warehouse;

import com.guanshiyun.warehouse.Warehouse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;



public interface WarehouseRepository extends ReactiveCrudRepository<Warehouse, Long> {
}
