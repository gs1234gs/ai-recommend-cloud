package com.guanshiyun.controller.apikey;

import com.guanshiyun.base.ApiKey;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("apikey/")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @GetMapping("find")
    Mono<ResultT<ApiKey>> findAll() {
        return apiKeyService.findApiKey()
                .map(spikey -> {
                    log.info("apikey findAll: {}", spikey);
                    return ResultT.success(spikey);
                });
    }
}
