package com.guanshiyun.repository.embedding;

import com.guanshiyun.embedding.ActiveSimilarityThresholdConfiguration;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


public interface ActiveSimilarityThresholdConfigurationRepository
        extends R2dbcRepository<ActiveSimilarityThresholdConfiguration, Long> {

    @Modifying
    @Query("UPDATE active_similarity_threshold_configuration SET similarity_threshold = :threshold WHERE id = :id")
    Mono<Void> updateSimilarityThresholdById(Long id, Double threshold);
}
