package com.guanshiyun.service.similarity.impl;

import com.db.cursorQuery.ReactivePageQuery;
import com.db.dbnumber.ConstNumber;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationPageVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationSaveVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationVO;
import com.guanshiyun.embedding.SimilarityThresholdStrategyConfiguration;
import com.guanshiyun.repository.embedding.ActiveSimilarityThresholdConfigurationRepository;
import com.guanshiyun.repository.embedding.SimilarityThresholdStrategyConfigurationRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.similarity.SimilarityThresholdStrategyConfigurationService;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SimilarityThresholdStrategyConfigurationServiceImpl
        implements SimilarityThresholdStrategyConfigurationService {

    private final SimilarityThresholdStrategyConfigurationRepository similarityThresholdStrategyConfigurationRepository;
    private final ActiveSimilarityThresholdConfigurationRepository  activeSimilarityThresholdConfigurationRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;

    @Override
    public Mono<Long> save(SimilarityThresholdStrategyConfigurationSaveVO saveVO) {
        SimilarityThresholdStrategyConfiguration similarityThresholdStrategyConfiguration =
                BeanConvertUtil.toBean(saveVO, SimilarityThresholdStrategyConfiguration.class);
        if(Objects.nonNull(similarityThresholdStrategyConfiguration.getId())){
            return r2dbcUpdateHelper.updateIgnoreNull(
                    SimilarityThresholdStrategyConfiguration.class,
                    similarityThresholdStrategyConfiguration,
                    SimilarityThresholdStrategyConfiguration.Fields.id
            );
        }
        return similarityThresholdStrategyConfigurationRepository.save(similarityThresholdStrategyConfiguration)
                .map(SimilarityThresholdStrategyConfiguration::getId);
    }

    @Override
    public Mono<SimilarityThresholdStrategyConfigurationVO> findById(Long id) {
        return similarityThresholdStrategyConfigurationRepository.findById(id)
                .map(item->BeanConvertUtil.toBean(item, SimilarityThresholdStrategyConfigurationVO.class));
    }

    @Override
    public Mono<PageResultT<List<SimilarityThresholdStrategyConfigurationVO>>> findAllByPageable(RequestPage<SimilarityThresholdStrategyConfigurationPageVO> requestPage) {
        return ReactivePageQuery.of(
                r2dbcEntityTemplate,
                SimilarityThresholdStrategyConfiguration.class,
                BeanConvertUtil.toBean(requestPage,SimilarityThresholdStrategyConfiguration.class)

        )
                .page()
                .map(page-> {
                    PageResultT<List<SimilarityThresholdStrategyConfigurationVO>> bean =
                            BeanConvertUtil.toBean(page, SimilarityThresholdStrategyConfigurationVO.class);
                    return bean;
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return similarityThresholdStrategyConfigurationRepository.deleteById(id);
    }

    @Override
    public Mono<Long> enableSimilarityThresholdStrategy(Long id) {
        return similarityThresholdStrategyConfigurationRepository.findById(id)
                .flatMap(similarity->{
                    Double similarityThreshold = similarity.getSimilarityThreshold();
                   return activeSimilarityThresholdConfigurationRepository
                           .updateSimilarityThresholdById(ConstNumber.LONG_ONE,similarityThreshold)
                           .then(Mono.fromCallable(() -> id));
                });
    }
}
