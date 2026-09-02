package com.guanshiyun.rpc.apikey;

import com.guanshiyun.base.ApiKey;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

public interface ApiKeyServiceApi {

    Mono<ResultT<ApiKey>> findApiKeyById();
}
