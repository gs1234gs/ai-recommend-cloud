package com.guanshiyun.rpc.behaviorapi.browse.apibehaviorimpl;

import com.guanshiyun.behaviorenums.BehaviorApiUrl;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.UserBrowseServiceApi;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import com.guanshiyun.rpc.config.WebClientRpc;
import com.guanshiyun.webutils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserBrowseServiceApiImpl implements UserBrowseServiceApi {
    private final WebClientRpc webClientRpc;

    @Override
    public Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows) {

        return webClientRpc
                .webClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BehaviorApiUrl.BROWSE_FIND_BY_ROWS)
                                .pathSegment()
                                .queryParam(BehaviorParamKey.ROWS, rows)// 参数2
                                .build())
                .retrieve()
                .bodyToMono(WebClientUtils.typeRef());

    }
}
