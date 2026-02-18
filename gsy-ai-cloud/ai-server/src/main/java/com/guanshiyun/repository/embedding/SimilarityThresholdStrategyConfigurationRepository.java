package com.guanshiyun.repository.embedding;

import com.guanshiyun.embedding.SimilarityThresholdStrategyConfiguration;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.math.BigInteger;

public interface SimilarityThresholdStrategyConfigurationRepository extends R2dbcRepository<SimilarityThresholdStrategyConfiguration, BigInteger> {
}
