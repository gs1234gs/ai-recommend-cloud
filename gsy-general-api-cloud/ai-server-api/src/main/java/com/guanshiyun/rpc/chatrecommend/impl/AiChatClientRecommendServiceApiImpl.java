package com.guanshiyun.rpc.chatrecommend.impl;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.rpc.config.WebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatClientRecommendServiceApiImpl implements AiChatClientRecommendServiceApi {
    private final WebClientRpc webClientRpc;
    @Override
    public Mono<ResultT<List<BigInteger>>> hostData() {
        return null;
    }
}
