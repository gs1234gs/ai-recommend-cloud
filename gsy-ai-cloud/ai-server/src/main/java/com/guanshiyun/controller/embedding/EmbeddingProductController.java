package com.guanshiyun.controller.embedding;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/embedding")
public class EmbeddingProductController {

    private final EmbeddingProductService embeddingProductService;
    /**
     *
     * 保存商品向量
     * @RequestBody List<ProductForEmbeddingApVO>  products
     * @return Mono<ResultT<List<String>>>
     *
     * */
    @PostMapping("/product/saveBatch")
    public Mono<ResultT<List<String>>> embeddingSaveProduct(@RequestBody List<ProductForEmbeddingApVO> products){
        return embeddingProductService.saveBatch( products)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }

    /**
     *
     * 删除商品向量
     * @PathVariable BigInteger  productId
     * @return Mono<ResultT<Void>>
     *
     * */
    @DeleteMapping("/product/deleteByProductId/{productId}")
    public Mono<ResultT<Void>> embeddingDeleteProduct(@PathVariable BigInteger productId){
        return embeddingProductService.deleteById(List.of(productId))
                .thenReturn(ResultT.success((Void) null))
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }

    @PostMapping("/product/recommendForUser")
    public Mono<ResultT<List<BigInteger>>> embeddingRecommendForUser(
            @RequestBody RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> recentProducts){
        return embeddingProductService.recommendForUser(recentProducts.getData(), recentProducts.getTopK())
                .map(ResultT::success)
                .doOnSuccess(ok->log.info("推荐商品列表： {}",ok.getData()))
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }
    /**
     * 给根关键字检索
     * */
    @GetMapping("/searchKeyWard")
    public Mono<ResultT<List<BigInteger>>> embeddingKeyWard(@RequestParam String keyWard,
                                                            @RequestParam(required = false,defaultValue = "10") Integer topK){
        return embeddingProductService.searchByKeyword(keyWard,topK)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }
}
