package com.guanshiyun.rpc.behaviorapi.search.impl;

import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.behaviorenums.BehaviorSearchApiUrlEnum;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.rpc.config.BehaviorWebClientRpc;
import com.guanshiyun.rpc.profile.SearchContentApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchServiceApiImpl implements UserSearchServiceApi {
    private final BehaviorWebClientRpc behaviorWebClientRpc;

    @Override
        public Mono<ResultT<List<SearchContentApi>>> findUserSearchRecord(Integer rows) {
        return behaviorWebClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorSearchApiUrlEnum.BEHAVIOR_FIND_BY_ROWS.getValue())
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<SearchContentApi>>>() {});

    }

    @Override
    public Mono<ResultT<BigInteger>> saveUserSearchRecord(SearchContentApi searchContentApi) {
        return behaviorWebClientRpc.webClient()
                .post()
                .uri(BehaviorSearchApiUrlEnum.BEHAVIOR_SAVE.getValue())
                .bodyValue(searchContentApi)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<BigInteger>>() {});
    }
}
