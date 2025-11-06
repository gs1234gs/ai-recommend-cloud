package com.guanshiyun.repository.product;

import com.guanshiyun.product.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface ProductRepository extends ReactiveCrudRepository<Product, BigInteger> {
}
