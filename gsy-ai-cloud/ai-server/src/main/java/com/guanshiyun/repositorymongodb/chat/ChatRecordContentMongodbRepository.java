package com.guanshiyun.repositorymongodb.chat;

import com.guanshiyun.mymongodb.ChatRecordContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface ChatRecordContentMongodbRepository extends ReactiveMongoRepository<ChatRecordContent, BigInteger> {
}
