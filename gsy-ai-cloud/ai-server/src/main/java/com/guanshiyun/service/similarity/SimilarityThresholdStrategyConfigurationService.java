package com.guanshiyun.service.similarity;

import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationPageVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationSaveVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SimilarityThresholdStrategyConfigurationService {
    Mono<Long> save(SimilarityThresholdStrategyConfigurationSaveVO saveVO);

    Mono<SimilarityThresholdStrategyConfigurationVO> findById(Long id);

    Mono<PageResultT<List<SimilarityThresholdStrategyConfigurationVO>>> findAllByPageable(RequestPage<SimilarityThresholdStrategyConfigurationPageVO> requestPage);

    Mono<Void> deleteById(Long id);
    //启用推荐参数
    Mono<Long> enableSimilarityThresholdStrategy(Long id);
}
