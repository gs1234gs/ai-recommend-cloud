package com.guanshiyun.repository.search;

import com.guanshiyun.search.UserSearchMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface UserSearchMongodbRepository extends ReactiveMongoRepository<UserSearchMongodb, BigInteger> {
}
