package com.guanshiyun.rpc.behaviorapi.apibehaviorimpl;

import com.guanshiyun.behaviorenums.BehaviorApiUrl;
import com.guanshiyun.behaviorenums.BehaviorParamKey;
import com.guanshiyun.behaviorenums.BehaviorPrefix;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.UserBrowseServiceApi;
import com.guanshiyun.rpc.behaviorapi.vo.UserBrowseVOApi;
import com.guanshiyun.webutils.WebClientUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class UserBrowseServiceApiImpl implements UserBrowseServiceApi {
   private final WebClient.Builder webClientBuilder;
    // 构造函数注入
    public UserBrowseServiceApiImpl( WebClient.Builder webClientBuilder) {

        this.webClientBuilder = webClientBuilder.baseUrl(BehaviorPrefix.BASE_URL);
    }
    @Override
    public Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows) {
       return webClientBuilder
               .build()
               .get()
               .uri(uriBuilder ->
                       uriBuilder
                               .path(BehaviorApiUrl.BROWSE_FIND_BY_ROWS)
                               .queryParam(BehaviorParamKey.ROWS, 10)// 参数2
                               .build())
               .retrieve()
               .bodyToMono(WebClientUtils.<ResultT<List<UserBrowseVOApi>>>typeRef());

    }
}
