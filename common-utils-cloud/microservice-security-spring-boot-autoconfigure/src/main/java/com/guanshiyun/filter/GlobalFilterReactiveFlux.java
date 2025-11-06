package com.guanshiyun.filter;

import com.alibaba.fastjson.JSONObject;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.consts.ConstHeaderLocals;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.consts.PublicEndpoints;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class AiFilter implements WebFilter {

    private final MyBigInteger myBigInteger;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        RequestPath path = exchange.getRequest().getPath();
        if(PublicEndpoints.PERMSSION_WHITE_LIST.contains(path.toString())){
            log.info("这是白名单，放行：{}",path);
            return chain.filter(exchange);
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        log.info("请求头：{}", headers);
        String userJson = headers.getFirst(ConstHeaderLocals.USER_INFO_KEY);
        if (StringUtil.isNullOrEmpty(userJson)) {
            log.warn("用户信息为空：{}", userJson);
            ServerHttpResponse response = exchange.getResponse();
            return response.writeWith(Mono.just(
                    response.bufferFactory()
                            .wrap(JSONObject.toJSONString(
                                            ResultT
                                                    .<Object>builder()
                                                    .code(401)
                                                    .msg("未登陆")
                                                    .data(userJson)
                                                    .build()
                                    ).getBytes(StandardCharsets.UTF_8)
                            )
            ));
        }
        Map userMap = JSONObject.parseObject(userJson, Map.class);
        BigInteger userId = myBigInteger.bigInteger(
                userMap.get(
                        ConstMapClassNickName.MAP_USERID_KEY
                ));
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(
                        ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,
                        userId
                ));

    }
}
