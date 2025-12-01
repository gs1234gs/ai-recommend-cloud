package com.guanshiyun.security.handler;

import cn.hutool.json.JSONUtil;
import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.responsepojo.ResultT;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class HandleException {
    public static ServerAuthenticationEntryPoint handleUnauthorized() {
        return (exchange, e) -> exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(
                        JSONUtil.toJsonStr(ResultT.builder()
                                        .code(HttpCodeConst.FORBIDDEN)
                                        .msg("未授权")
                                        .data(null))
                                .getBytes(StandardCharsets.UTF_8)
                )
        ));
    }

    public static ServerAccessDeniedHandler handleForbidden() {
        return (exchange, e) -> exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(
                        JSONUtil.toJsonStr(
                                ResultT.builder()
                                        .code(HttpCodeConst.FORBIDDEN)
                                        .msg("访问被拒绝")
                                        .data(null)
                                        .build()
                        ).getBytes(StandardCharsets.UTF_8)
                )
        ));
    }
}
