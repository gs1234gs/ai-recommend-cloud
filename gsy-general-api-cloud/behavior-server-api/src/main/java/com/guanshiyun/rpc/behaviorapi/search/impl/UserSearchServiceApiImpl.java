package com.guanshiyun.rpc.behaviorapi.search.impl;

import com.guanshiyun.behaviorenums.BehaviorApiUrl;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.behaviorenums.BehaviorPrefix;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.webutils.WebClientUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserSearchServiceApiImpl implements UserSearchServiceApi {
    private final WebClient.Builder webClientBuilder;
    public UserSearchServiceApiImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder.baseUrl(BehaviorPrefix.BASE_URL);
    }

    @Override
    public Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows) {
        return webClientBuilder
                .build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorApiUrl.SEARCH_FIND_BY_ROWS)
                                .queryParam(BehaviorParamKey.ROWS, 10)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(WebClientUtils.<ResultT<List<UserBrowseVOApi>>>typeRef());

    }
}
