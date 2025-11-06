package com.guanshiyun.repository.click;

import com.guanshiyun.click.UserClickMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface UserClickMongodbRepository extends ReactiveMongoRepository<UserClickMongodb, BigInteger> {
}
