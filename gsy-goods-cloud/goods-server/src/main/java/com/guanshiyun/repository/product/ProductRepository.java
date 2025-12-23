package com.guanshiyun.repository.product;

import com.guanshiyun.product.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.math.BigInteger;

public interface ProductRepository extends R2dbcRepository<Product, BigInteger> {
}
