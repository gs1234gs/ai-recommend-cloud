package com.guanshiyun.repositorymongodb.konwledge;

import com.guanshiyun.mymongodb.KnowledgeRepositoryContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;



public interface KnowledgeRepositoryContentRepository extends ReactiveMongoRepository<KnowledgeRepositoryContent, Long> {
}
