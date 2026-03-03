package com.guanshiyun.rpc.behaviorapi.browse.impl;

import com.guanshiyun.behaviorenums.BehaviorBrowseApiUrlEnum;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.UserBrowseServiceApi;
import com.guanshiyun.rpc.config.BehaviorWebClientRpc;
import com.guanshiyun.rpc.profile.BrowseProfileApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserBrowseServiceApiImpl implements UserBrowseServiceApi {
    private final BehaviorWebClientRpc behaviorWebClientRpc;

    @Override
    public Mono<ResultT<List<BrowseProfileApi>>> findUserBrowseRecord(Integer rows) {

        return behaviorWebClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorBrowseApiUrlEnum.BROWSE_FIND_BY_ROWS.getValue())
                                .pathSegment()
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<BrowseProfileApi>>>() {});

    }
}
