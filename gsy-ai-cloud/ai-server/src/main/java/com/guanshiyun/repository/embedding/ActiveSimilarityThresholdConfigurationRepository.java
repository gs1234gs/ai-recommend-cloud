package com.guanshiyun.repository.embedding;

import com.guanshiyun.embedding.ActiveSimilarityThresholdConfiguration;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.math.BigInteger;

public interface ActiveSimilarityThresholdConfigurationRepository extends R2dbcRepository<ActiveSimilarityThresholdConfiguration, BigInteger> {
}
