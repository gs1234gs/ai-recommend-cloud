package com.guanshiyun.controller.similarity;

import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationPageVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationSaveVO;
import com.guanshiyun.controller.similarity.vo.SimilarityThresholdStrategyConfigurationVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.similarity.SimilarityThresholdStrategyConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/similarity/")
@RequiredArgsConstructor
public class SimilarityThresholdStrategyConfigurationController {
    private final SimilarityThresholdStrategyConfigurationService similarityThresholdStrategyConfigurationService;

    //保存
    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody SimilarityThresholdStrategyConfigurationSaveVO saveVO) {
        return similarityThresholdStrategyConfigurationService.save(saveVO)
                .map(ResultT::success);
    }
    //根据id
    @GetMapping("findById/{id}")
    public Mono<ResultT<SimilarityThresholdStrategyConfigurationVO>> findById(@PathVariable Long id) {
        return similarityThresholdStrategyConfigurationService.findById(id)
                .map(ResultT::success);

    }
    //分页
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<SimilarityThresholdStrategyConfigurationVO>>>> findPage(@RequestBody RequestPage<SimilarityThresholdStrategyConfigurationPageVO> requestPage) {
        return similarityThresholdStrategyConfigurationService.findAllByPageable(requestPage)
                .map(ResultT::success);
    }

    //删除
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteById(@PathVariable Long id) {
        return similarityThresholdStrategyConfigurationService.deleteById(id)
                .then(Mono.fromCallable(ResultT::success));
    }
    //启用相似度
    @GetMapping("/enableSimilarity/{id}")
    public Mono<ResultT<Long>> enableSimilarity(@PathVariable Long id) {
       return similarityThresholdStrategyConfigurationService.enableSimilarityThresholdStrategy(id)
                .map(ResultT::success);
    }

}
