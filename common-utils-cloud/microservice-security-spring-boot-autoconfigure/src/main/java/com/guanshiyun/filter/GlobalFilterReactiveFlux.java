package com.guanshiyun.filter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.consts.ConstHeaderLocals;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.consts.PublicEndpoints;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.OnDisableBusinessWebFilterCondition;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
@Conditional(OnDisableBusinessWebFilterCondition.class)
public class GlobalFilterReactiveFlux implements WebFilter {

    private final MyLong myLong;
    private final ObjectMapper objectMapper;


    @SneakyThrows
    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if(true){
            return chain.filter(exchange);
        }
        //获取用户信息
        RequestPath path = exchange.getRequest().getPath();
        if (PublicEndpoints.PERMSSION_WHITE_LIST.contains(path.value())) {
            log.info("这是白名单，放行：{}", path);
            return chain.filter(exchange);
        }
        // 2. 前缀匹配
        for (String prefix : PublicEndpoints.PERMISSION_WHITE_PREFIX_LIST) {
            if (path.value().startsWith(prefix)) {
                log.info("白名单（前缀匹配）:{}", path);
                return chain.filter(exchange);
            }
        }

        //获取用户信息
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String traceId = headers.getFirst(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY);
        String userIdStr = headers.getFirst(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
        String tenantIdStr = headers.getFirst(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY);
        log.info("traceId : {} , userId : {}", traceId, userIdStr);
        if (StringUtils.hasText(traceId) || StringUtils.hasText(userIdStr)) {
            log.info("这是特殊请求，放行：{}", path);
            return chain.filter(exchange)
                    .contextWrite(ctx -> {
                        if (StringUtils.hasText(userIdStr)) {
                            ctx = ctx.put(
                                            ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,
                                            myLong.myLong(userIdStr)
                                    )
                                    .put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY, myLong.myLong(tenantIdStr));
                        }
                        if (StringUtils.hasText(traceId)) {
                            ctx = ctx.put(
                                            ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY,
                                            traceId
                                    )
                                    .put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY, myLong.myLong(tenantIdStr));
                        }
                        return ctx;
                    });
        }

//        log.info("请求头：{}", headers);
        String userJson = headers.getFirst(ConstHeaderLocals.USER_INFO_KEY);
        if (StringUtil.isNullOrEmpty(userJson)) {
            log.warn("用户信息为空：{}", userJson);
            ServerHttpResponse response = exchange.getResponse();
            return response.writeWith(Mono.just(
                    response.bufferFactory()
                            .wrap(
                                    objectMapper.writeValueAsBytes(
                                            ResultT
                                                    .<String>builder()
                                                    .code(401)
                                                    .msg("未登陆")
                                                    .data(userJson)
                                                    .build()
                                    )
//                                    JSONObject.toJSONString(
//                                            ResultT
//                                                    .<Object>builder()
//                                                    .code(401)
//                                                    .msg("未登陆")
//                                                    .data(userJson)
//                                                    .build()
//                                    ).getBytes(StandardCharsets.UTF_8)
                            )
            ));
        }
//        if(StringUtil.isNullOrEmpty(userJson)){
//            log.warn("用户信息为空：{}", userJson);
//            //放行，可能是游客，后续优化
//            return chain.filter(exchange);
//        }
        log.info("用户信息：{}", userJson);
//        var userMap = JSONObject.parseObject(userJson, Map.class);
        Map<String,Object> userMap = objectMapper.readValue(userJson,new TypeReference<Map<String, Object>>() {});
        if (Objects.isNull(userMap)) {
            log.warn("用户信息为空：{}", userJson);
            //不放行
            return Mono.error(new RuntimeException("用户信息为空"));
        }
        Long userId = myLong.myLong(
                userMap.get(
                        ConstMapClassNickName.MAP_USERID_KEY
                ));
        Long tenantId = myLong.longOrNull(
                userMap.get(
                        ConstMapClassNickName.MAP_TENANT_ID_RESPONSE_KEY
                )
        );
        //设置用户id到链路中
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(
                        ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,
                        userId
                ).put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY, tenantId));

    }
}
