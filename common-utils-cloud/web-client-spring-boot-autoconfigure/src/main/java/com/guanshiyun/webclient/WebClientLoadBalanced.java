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

import java.util.Objects;

@Configuration
public class WebClientLoadBalanced {
    /**
     * 全局首选的 WebClient.Builder Bean
     * 别名：defaultWebClientBuilder（语义化命名，明确标识这是默认/首选的构建器）
     * @return WebClient.Builder 实例
     */
    @Bean("defaultWebClientBuilder")  // 自定义别名，语义清晰
    @Primary                           // 保留首选标记，解决多Bean冲突
    public WebClient.Builder webClientBuilder() {
        // 基础配置：可根据需要扩展默认配置（如默认超时、默认请求头）
        return WebClient.builder()
                // 可选：添加全局默认配置，增强实用性
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)); // 调整默认内存缓冲区大小
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

    @Bean
    @LoadBalanced
    @Qualifier("uploadWebClientBuilder")
    public WebClient.Builder uploadWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(ServerName.UPLOAD_APP.getValue())
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx ->
                                Mono.just(clientRequest(ctx, clientRequest))
                        )
                ));
    }

    public ClientRequest clientRequest(ContextView ctx, ClientRequest clientRequest) {
        // 从 Reactor Context 中取 userId
        String userIdL = ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY;
        Object userId = ctx.getOrDefault(userIdL, "");

        // 从 Reactor Context 中取 traceId
        String teaceId = ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY;
        Object traceId = ctx.getOrDefault(teaceId, teaceId);

        String localTenantIdKey = ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY;
        Object tenantId = ctx.getOrDefault(localTenantIdKey, "1") ;

        userId = Objects.nonNull(userId) ? userId : "";
        traceId = Objects.nonNull(traceId) ? traceId : teaceId;
        tenantId = Objects.nonNull(tenantId) ? tenantId : "1";


        // 构建新的 ClientRequest 并设置两个 header
        return ClientRequest.from(clientRequest)
                .header(userIdL, userId.toString())
                .header(teaceId, traceId.toString())
                .header(localTenantIdKey, tenantId.toString())
                .build();
    }

}
