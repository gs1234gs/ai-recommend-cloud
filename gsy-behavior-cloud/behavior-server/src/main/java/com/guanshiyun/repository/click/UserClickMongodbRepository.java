package com.guanshiyun.repository.click;

import com.guanshiyun.click.UserClickMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;



public interface UserClickMongodbRepository extends ReactiveMongoRepository<UserClickMongodb, Long> {
}
