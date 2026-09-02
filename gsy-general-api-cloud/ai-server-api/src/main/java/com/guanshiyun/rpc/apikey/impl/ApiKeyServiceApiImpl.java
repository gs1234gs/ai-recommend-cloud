package com.guanshiyun.rpc.apikey.impl;

import com.guanshiyun.aienums.AiApiUrl;
import com.guanshiyun.base.ApiKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apikey.ApiKeyServiceApi;
import com.guanshiyun.rpc.config.AiWebClientRpc;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ApiKeyServiceApiImpl implements ApiKeyServiceApi {
    private final AiWebClientRpc aiWebClientRpc;

    @Override
    public Mono<ResultT<ApiKey>> findApiKeyById() {
        return aiWebClientRpc.webClient()
                .get()
                .uri(AiApiUrl.FIND_API_KEY_URL)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<ApiKey>>() {});
    }
}
