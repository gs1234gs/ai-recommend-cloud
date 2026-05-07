package com.guanshiyun.controller.embedding;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.items.Item;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.service.embedding.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/embedding/product")
public class EmbeddingProductController {

    private final EmbeddingProductService embeddingProductService;
    /**
     *
     * 保存商品向量
     * @RequestBody List<ProductForEmbeddingApVO>  products
     * @return Mono<ResultT<List<String>>>
     *
     * */
    @PostMapping("/saveBatch")
    public Mono<ResultT<List<String>>> embeddingSaveProduct(@RequestBody List<ProductForEmbeddingApVO> products){
        return embeddingProductService.saveBatch( products)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }

    /**
     *
     * 删除商品向量
     * @PathVariable Long  productId
     * @return Mono<ResultT<Void>>
     *
     * */
    @DeleteMapping("/deleteByProductId/{productId}")
    public Mono<ResultT<Void>> embeddingDeleteProduct(@PathVariable Long productId){
        return embeddingProductService.deleteById(List.of(productId))
                .thenReturn(ResultT.success((Void) null))
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }

    @PostMapping("/recommendForUser")
    public Mono<ResultT<List<Long>>> embeddingRecommendForUser(
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
    public Mono<ResultT<List<Long>>> embeddingKeyWard(@RequestParam String keyWard,
                                                            @RequestParam(required = false,defaultValue = "10") Integer topK){
        return embeddingProductService.searchByKeyword(keyWard,topK)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error()));
    }
    //gorse推荐
    @GetMapping("gorse/{userId}/{n}")
    public Mono<ResultT<List<String>>>  gorse(@PathVariable String userId,@PathVariable(required = false) Integer n){
      return   embeddingProductService.gorse(userId, n)
              .map(ResultT::success)
              .onErrorResume(throwable -> Mono.just(ResultT.error(throwable.getMessage())));
    }

    //gorseItem保存
    @PostMapping("/gorseItem")
    public Mono<ResultT<RowAffected>> gorseItem(@RequestBody Item item){
        return embeddingProductService.saveGorse( item)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error(throwable.getMessage())));
    }

    @DeleteMapping("/gorseItem/{itemId}")
    public Mono<ResultT<RowAffected>> gorseItemDelete(@PathVariable String itemId){
        return embeddingProductService.deleteItem(itemId)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error(throwable.getMessage())));
    }

}
