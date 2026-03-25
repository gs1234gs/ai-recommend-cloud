package com.guanshiyun.rpc.chatrecommend.impl;

import com.guanshiyun.aienums.AiApiUrl;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.responsepojo.ResultT;
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
//    private final WebClientRpc webClientRpc;
//    @Override
//    public Mono<ResultT<List<Item>>> hostData(List<Item>  itemList) {
//        return null;
//    }
}
