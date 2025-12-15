package com.guanshiyun.service.embedding.product.impl;

import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigInteger;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingProductServiceImpl implements EmbeddingProductService {
    private final VectorStore vectorStore;
    private final MyBigInteger myBigInteger;

    /**
     * 批量保存商品向量
     */
    @Override
    public Flux<Object> saveBatch(List<ProductForEmbeddingApVO> products) {

        return Flux.fromIterable(products)
                .map(product ->
                        Document.builder()
                                .id(product.getId().toString())
                                .text(product.recommendEmbeddingText())   // 只放语义
                                .metadata(product.metadata())             // 非语义信息
                                .build()
                )
                .collectList()
                .doOnNext(vectorStore::add) // 统一交给 VectorStore
                .flatMapMany(docs ->
                        Flux.fromIterable(docs)
                                .map(Document::getId)
                );
    }

    /**
     * 删除商品及其向量
     */
    @Override
    public Mono<Void> deleteById(List<BigInteger> idList) {
        return Mono.fromCallable(() -> {
                    vectorStore.delete(
                            idList
                                    .stream()
                                    .map(BigInteger::toString)
                                    .toList()
                    );
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * 实时推荐：基于用户最近浏览的商品向量，搜索，点击，购买，收藏
     */
    @Override
    public Mono<List<BigInteger>> recommendForUser(List<ProductForEmbeddingApVO> recentProducts, int topK) {
        return Flux.fromIterable(recentProducts)
                .flatMap(product -> {
                    String query = product.recommendEmbeddingText();
                    SearchRequest request = SearchRequest.builder()
                            .query(query)
                            .similarityThreshold(0.5f)
                            .topK(topK)
                            .build();
                    // 包装同步调用为 Mono 异步
                    return Mono.fromCallable(() -> vectorStore.similaritySearch(request))
                            .subscribeOn(Schedulers.boundedElastic())  // 阻塞操作放到 boundedElastic
                            .flatMapMany(Flux::fromIterable);
                })
                .distinct(Document::getId)   // 去重，避免多个 recentProduct 返回重复推荐
                .take(topK)    // 最终返回 topK
                .map(document -> myBigInteger.bigInteger(document.getId()))
                .collectList();
    }

}
