package com.guanshiyun.rpc.chatrecommend.impl;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.rpc.config.AiWebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatClientRecommendServiceApiImpl implements AiChatClientRecommendServiceApi {
    private final AiWebClientRpc aiWebClientRpc;
    @Override
    public Mono<ResultT<BigInteger>> embeddingProduct(List<ProductForEmbeddingApVO> product) {
        return aiWebClientRpc.webClient()
                .post()
                .uri("")
                .bodyValue( product)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
//    private final WebClientRpc webClientRpc;
//    @Override
//    public Mono<ResultT<List<Item>>> hostData(List<Item>  itemList) {
//        return null;
//    }
}
