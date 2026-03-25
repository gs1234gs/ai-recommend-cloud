package com.guanshiyun.rpc.behaviorapi.click.impl;

import com.guanshiyun.behaviorenums.BehaviorClickApiUrlEnum;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apisave.UserClickSaveApiVO;
import com.guanshiyun.rpc.behaviorapi.click.UserClickServiceApi;
import com.guanshiyun.rpc.config.BehaviorWebClientRpc;
import com.guanshiyun.rpc.profile.ClickProfileApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserClickServiceApiImpl implements UserClickServiceApi {
    private final BehaviorWebClientRpc behaviorWebClientRpc;

    @Override
    public Mono<ResultT<List<ClickProfileApi>>> findUserClickRecord(Integer rows) {

        return Mono.deferContextual(ctx->{
            return behaviorWebClientRpc
                    .webClient()
                    .get()

                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(BehaviorClickApiUrlEnum.CLICK_FIND_BY_ROWS.getValue())
                                    .queryParam(BehaviorParamKey.ROWS, rows)// 参数
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResultT<List<ClickProfileApi>>>() {});
        });

    }

    @Override
    public Mono<ResultT<Long>> saveUserClickRecord(UserClickSaveApiVO userClickSaveApiVO) {
        return Mono.deferContextual(ctx->{
            return behaviorWebClientRpc
                    .webClient()
                    .post()
                    .uri(BehaviorClickApiUrlEnum.CLICK_SAVE.getValue())
                    .body(Mono.just(userClickSaveApiVO), UserClickSaveApiVO.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResultT<Long>>() {});
        });

    }

}
