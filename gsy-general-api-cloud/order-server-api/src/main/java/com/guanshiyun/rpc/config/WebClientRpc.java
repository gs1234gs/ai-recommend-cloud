package com.guanshiyun.rpc.config;


import com.guanshiyun.aienums.OrderPrefix;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientRpc {
    private final WebClient webClient;
    // 构造函数注入
    public WebClientRpc(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder
                .clone()
                .baseUrl(OrderPrefix.BASE_URL)
                .build();
    }

    public WebClient webClient() {
        return webClient;
    }
    public WebClient webClient( String baseUrl) {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build();
    }

}
