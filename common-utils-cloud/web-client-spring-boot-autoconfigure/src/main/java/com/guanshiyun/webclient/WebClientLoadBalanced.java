package com.guanshiyun.webclient;

import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientLoadBalanced {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest ->
                        Mono.deferContextual(ctx -> {
                            // 从 Reactor Context 中取 userId
                            String userId = ctx.getOrDefault(
                                    ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,
                                    ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY   // 默认值（你也可以换成 traceId）
                            );

                            ClientRequest newRequest = ClientRequest.from(clientRequest)
                                    .header(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId)
                                    .build();

                            return Mono.just(newRequest);
                        })
                ));
    }
}
