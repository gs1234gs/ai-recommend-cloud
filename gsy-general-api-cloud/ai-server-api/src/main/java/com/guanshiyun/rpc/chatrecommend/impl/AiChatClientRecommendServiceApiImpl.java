package com.guanshiyun.rpc.chatrecommend.impl;

import com.guanshiyun.aienums.AiApiUrl;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.items.Item;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.rpc.config.AiWebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatClientRecommendServiceApiImpl implements AiChatClientRecommendServiceApi {
    private final AiWebClientRpc aiWebClientRpc;
    @Override
    public Mono<ResultT<List<String>>> embeddingProduct(List<ProductForEmbeddingApVO> product) {
        return aiWebClientRpc.webClient()
                .post()
                .uri(AiApiUrl.EMBEDDING_PRODUCT_SAVE_BATCH)
                .bodyValue( product)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<String>>>() {});
    }

    @Override
    public Mono<ResultT<List<Long>>> recommendProduct(RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> recentProducts) {
        return aiWebClientRpc.webClient()
                .post()
                .uri(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_FOR_USER)
                .bodyValue(recentProducts)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<Long>>>() {});
    }

    @Override
    public Mono<ResultT<Void>> embeddingDeleteProduct(Long productId) {
        return aiWebClientRpc.webClient()
                .delete()
                .uri(AiApiUrl.EMBEDDING_PRODUCT_DELETE_BY_PRODUCT_ID, productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<Void>>() {});
    }

    @Override
    public Mono<ResultT<List<Long>>> searchByKeyword(String keyWard, Integer topK) {
        return aiWebClientRpc.webClient()
                .get()
                .uri(build->build.path(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_BY_KEY_WARD)
                        .queryParam("keyWard",keyWard)
                        .queryParam("topK",topK)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<Long>>>() {});
    }

    @Override
    public Mono<ResultT<List<String>>> gorse(String userId, int n) {
        return aiWebClientRpc.webClient()
                .get()
                .uri(build->build.path(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_BY_GORSE_N)
                        .build(userId,n))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<String>>>() {});
    }
    @Override
    public Mono<ResultT<List<String>>> gorse(String userId) {
        return aiWebClientRpc.webClient()
                .get()
                .uri(build->build.path(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_BY_GORSE)
                        .build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<String>>>() {});
    }

    @Override
    public Mono<ResultT<RowAffected>> gorse(Item item) {
        return aiWebClientRpc.webClient()
                .post()
                .uri(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_BY_GORSE_SAVE)
                .bodyValue( item)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<RowAffected>>() {});
    }

    @Override
    public Mono<ResultT<RowAffected>> deleteGorse(String itemId) {
        return aiWebClientRpc
                .webClient()
                .delete()
                .uri(build->build.path(AiApiUrl.EMBEDDING_PRODUCT_RECOMMEND_BY_GORSE_DELETE)
                        .build(itemId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<RowAffected>>() {});
    }
//    private final WebClientRpc webClientRpc;
//    @Override
//    public Mono<ResultT<List<Item>>> hostData(List<Item>  itemList) {
//        return null;
//    }
}
