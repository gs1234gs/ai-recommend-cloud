package com.guanshiyun.webclient;


import com.guanshiyun.servername.ServerName;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@Configuration
public class WebClientLoadBalanced {
    @Bean
    @Primary  // 关键：标记为首选
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @LoadBalanced
    @Qualifier("aiWebClientBuilder")
    public WebClient.Builder aiWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.AI_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }

    @Bean
    @LoadBalanced
    @Qualifier("goodsWebClientBuilder")
    public WebClient.Builder goodsWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.GOODS_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }

    @Bean
    @LoadBalanced
    @Qualifier("behaviorWebClientBuilder")
    public WebClient.Builder behaviorWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.BEHAVIOR_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }
    @Bean
    @LoadBalanced
    @Qualifier("systemWebClientBuilder")
    public WebClient.Builder systemWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.SYSTEM_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }

    @Bean
    @LoadBalanced
    @Qualifier("orderWebClientBuilder")
    public WebClient.Builder orderWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.ORDER_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }

    public ClientRequest clientRequest(ContextView ctx, ClientRequest clientRequest) {
        // 从 Reactor Context 中取 userId
        String userId = ctx.getOrDefault(
                ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,
                ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY   // 默认值（你也可以换成 traceId）
        );

        return ClientRequest.from(clientRequest)
                .header(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId)
                .build();
    }
}
