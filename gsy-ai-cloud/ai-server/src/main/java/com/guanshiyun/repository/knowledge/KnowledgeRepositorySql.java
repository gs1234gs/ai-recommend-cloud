package com.guanshiyun.repository.knowledge;

import com.guanshiyun.knowledge.KnowledgeRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface KnowledgeRepositorySql extends ReactiveCrudRepository<KnowledgeRepository, BigInteger> {
}
