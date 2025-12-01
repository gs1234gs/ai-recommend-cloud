package com.guanshiyun.rpc.behaviorapi.search.impl;

import com.guanshiyun.behaviorenums.BehaviorApiUrl;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchServiceApiImpl implements UserSearchServiceApi {
    private final WebClientRpc webClientRpc;

    @Override
    public Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows) {
        return webClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorApiUrl.SEARCH_FIND_BY_ROWS)
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());

    }
}
