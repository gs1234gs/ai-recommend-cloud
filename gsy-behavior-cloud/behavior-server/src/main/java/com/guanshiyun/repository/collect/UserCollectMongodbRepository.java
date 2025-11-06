package com.guanshiyun.repository.collect;

import com.guanshiyun.collect.UserCollectMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface UserCollectMongodbRepository extends ReactiveMongoRepository<UserCollectMongodb, BigInteger> {
}
