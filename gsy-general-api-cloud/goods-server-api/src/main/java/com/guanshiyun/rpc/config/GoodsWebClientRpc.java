package com.guanshiyun.rpc.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GoodsWebClientRpc {
    private final WebClient webClient;
    // 构造函数注入
    public GoodsWebClientRpc(@Qualifier("goodsWebClientBuilder") WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder
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
