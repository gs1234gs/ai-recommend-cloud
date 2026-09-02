package com.guanshiyun.service.apikey;

import com.guanshiyun.base.ApiKey;
import reactor.core.publisher.Mono;

public interface ApiKeyService {

    Mono<ApiKey> findApiKey();

}
