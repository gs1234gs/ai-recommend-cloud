package com.guanshiyun.security.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.responsepojo.ResultT;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class HandleException {
   public static ObjectMapper mapper = new ObjectMapper();

    public static ServerAuthenticationEntryPoint handleUnauthorized() {
        return (exchange, e) -> {
            try
            {
                return exchange.getResponse().writeWith(Mono.just(
                        exchange.getResponse().bufferFactory().wrap(
                                mapper.writeValueAsString(ResultT.builder()
                                                .code(HttpStatus.FORBIDDEN.value())
                                                .msg("未授权")
                                                .data(null))
                                        .getBytes(StandardCharsets.UTF_8)
                        )
                ));
            }
            catch (JsonProcessingException ex)
            {
                throw new RuntimeException(ex);
            }
        };
    }

    public static ServerAccessDeniedHandler handleForbidden() {
        return (exchange, e) -> {
            try
            {
                return exchange.getResponse().writeWith(Mono.just(
                        exchange.getResponse().bufferFactory().wrap(
                                mapper.writeValueAsString(
                                        ResultT.builder()
                                                .code(HttpStatus.FORBIDDEN.value())
                                                .msg("访问被拒绝")
                                                .data(null)
                                                .build()
                                ).getBytes(StandardCharsets.UTF_8)
                        )
                ));
            }
            catch (JsonProcessingException ex)
            {
                throw new RuntimeException(ex);
            }
        };
    }
}
