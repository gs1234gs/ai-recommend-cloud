package com.guanshiyun.service.apikey.apikey;

import com.guanshiyun.base.ApiKey;
import com.guanshiyun.repository.ApiKeyRepository;
import com.guanshiyun.service.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public Mono<ApiKey> findApiKey() {
        return apiKeyRepository.findAll()
                .take(1)
                .next();
    }
}
