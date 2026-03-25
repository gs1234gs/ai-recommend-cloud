package com.guanshiyun.repository.browse;

import com.guanshiyun.browse.UserBrowseMongodb;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;



public interface UserBrowseMongodbRepository extends ReactiveMongoRepository<UserBrowseMongodb, Long> {
}
