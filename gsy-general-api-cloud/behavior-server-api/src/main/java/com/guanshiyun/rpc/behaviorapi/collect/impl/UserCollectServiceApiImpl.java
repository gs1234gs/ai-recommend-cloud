package com.guanshiyun.rpc.behaviorapi.collect.impl;

import com.guanshiyun.behaviorenums.BehaviorApiUrl;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import com.guanshiyun.rpc.behaviorapi.collect.UserCollectServiceApi;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.utils.WebContextUtils;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCollectServiceApiImpl implements UserCollectServiceApi {
    private final WebClientRpc webClientRpc;
    private final WebContextUtils webContextUtils;
    @Override
    public Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows) {
        return webClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorApiUrl.COLLECT_FIND_BY_ROWS)
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());

    }
}
