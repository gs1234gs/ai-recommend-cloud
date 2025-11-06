package com.guanshiyun.repository.model;

import com.guanshiyun.bigmodel.BigModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface BigModelRepository extends ReactiveCrudRepository<BigModel, BigInteger> {
}
