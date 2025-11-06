package com.guanshiyun.repositorymongodb.konwledge;

import com.guanshiyun.mymongodb.KnowledgeRepositoryContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface KnowledgeRepositoryContentRepository extends ReactiveMongoRepository<KnowledgeRepositoryContent, BigInteger> {
}
