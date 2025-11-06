package com.guanshiyun.repository.browse;

import com.guanshiyun.browse.UserBrowseMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface UserBrowseMongodbRepository extends ReactiveMongoRepository<UserBrowseMongodb, BigInteger> {
}
