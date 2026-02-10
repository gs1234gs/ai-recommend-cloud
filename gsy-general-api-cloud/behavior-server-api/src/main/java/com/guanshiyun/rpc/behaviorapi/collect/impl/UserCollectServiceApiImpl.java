package com.guanshiyun.rpc.behaviorapi.collect.impl;

import com.guanshiyun.behaviorenums.BehaviorColleckApiUrlEnum;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.collect.UserCollectServiceApi;
import com.guanshiyun.rpc.config.BehaviorWebClientRpc;
import com.guanshiyun.rpc.profile.CollectProfileApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCollectServiceApiImpl implements UserCollectServiceApi {
    private final BehaviorWebClientRpc behaviorWebClientRpc;
    @Override
    public Mono<ResultT<List<CollectProfileApi>>> findUserCollectRecord(Integer rows) {
        return behaviorWebClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorColleckApiUrlEnum.COLLECT_FIND_BY_ROWS.getValue())
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<CollectProfileApi>>>() {});

    }
}
