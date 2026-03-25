package com.guanshiyun.repository.search;

import com.guanshiyun.search.UserSearchMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;



public interface UserSearchMongodbRepository extends ReactiveMongoRepository<UserSearchMongodb, Long> {
}
