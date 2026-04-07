package com.guanshiyun.filter;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.aliyun.oss.JwtUtils;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.consts.ConstHeaderLocals;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.consts.PublicEndpoints;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//拦截器
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayGlobalFilter implements GlobalFilter, Ordered {
    private final ReactiveRedisUtil reactiveRedisUtil;
    private final MyLong myLong;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        log.info("收到请求:{}", path);
        if (PublicEndpoints.PERMSSION_WHITE_LIST.contains(path)) {
            log.info("白名单：{}", path);
            return chain.filter(exchange);
        }
        // 2. 前缀匹配
        for (String prefix : PublicEndpoints.PERMISSION_WHITE_PREFIX_LIST) {
            if (path.startsWith(prefix)) {
                log.info("白名单（前缀匹配）:{}", path);
                return chain.filter(exchange);
            }
        }
        //不是白名单，拦截
        // ===== 特殊请求直通 =====
        String traceId = request.getHeaders().getFirst(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TRACE_ID_KEY);
        String userIdStr = request.getHeaders().getFirst(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
        if (StringUtils.hasText(traceId) || StringUtils.hasText(userIdStr)) {
            log.info("网关检测到特殊请求，放行：{}", path);
            return chain.filter(exchange);
        }
        log.info("黑名单：{}", path);
        String token = extractToken(request);
        if (StringUtil.isNullOrEmpty(token)) {
            for (String prefix : PublicEndpoints.RECOMMEND_WHITE_LIST) {
                if (path.startsWith(prefix)) {
                    log.info("白名单（前缀匹配）:{}", path);
                    return chain.filter(exchange);
                }
            }
            log.warn("token为空：{},未登陆", token);
            ServerHttpResponse response = exchange.getResponse();
            return response.writeWith(Mono.just(
                    response.bufferFactory()
                            .wrap(JSONObject.toJSONString(
                                            Result
                                                    .builder()
                                                    .code(401)
                                                    .msg("未登陆")
                                                    .build()
                                    ).getBytes(StandardCharsets.UTF_8)
                            )
            ));
        }
        Claims claims = null;
        //获取map，拿到用户信息，继续验证
        Map<String, Object> map = null;
        try {
            //解析token，获取用户信息
            claims = JwtUtils.checkToken(token);
            map = JwtUtils.getMap(claims);
        } catch (Exception e) {
            log.warn("登陆已经过期，{}", token, e);
            ServerHttpResponse response = exchange.getResponse();
            return response.writeWith(Mono.just(
                    response.bufferFactory()
                            .wrap(JSONObject.toJSONString(
                                            Result
                                                    .builder()
                                                    .code(401)
                                                    .msg("登陆已经过期")
                                                    .build()
                                    ).getBytes(StandardCharsets.UTF_8)
                            )
            ));
        }
        //获取用户id
        Long userId = myLong.myLong(claims.getSubject());
        Object obj = map.get(ConstMapClassNickName.MAP_USERTYPE_KEY);
        short userType = obj != null ? ((Number) obj).shortValue() : 0;
        Object tenantIdObj = map.getOrDefault(ConstMapClassNickName.MAP_TENANT_ID_RESPONSE_KEY, null);
        return reactiveRedisUtil.hGet(
                        ConstClassNickName.REDIS_TOKEN_KEY,
                        userId.toString()
                )
                .defaultIfEmpty( "")
                .flatMap(redisToken -> {
                    if(StringUtil.isNullOrEmpty(redisToken)){
                        log.error("登陆已经过期: {}", userId);
                        ServerHttpResponse response = exchange.getResponse();
                        return response.writeWith(Mono.just(
                                response.bufferFactory()
                                        .wrap(JSONObject.toJSONString(
                                                        Result
                                                                .builder()
                                                                .code(401)
                                                                .msg("登陆已经过期")
                                                                .build()
                                                ).getBytes(StandardCharsets.UTF_8)
                                        )
                        ));
                    }
                    if (!token.equals(JSONObject.parseObject(redisToken,String.class))) {
                        log.error("Token与Redis中不一致，userId: {}", userId);
                        ServerHttpResponse response = exchange.getResponse();
                        return response.writeWith(Mono.just(
                                response.bufferFactory()
                                        .wrap(JSONObject.toJSONString(
                                                        Result
                                                                .builder()
                                                                .code(401)
                                                                .msg("登陆已经过期")
                                                                .build()
                                                ).getBytes(StandardCharsets.UTF_8)
                                        )
                        ));
                    }
//                    return reactiveRedisUtil.hGet(
//                                    ConstClassNickName.REDIS_PERMISSION_KEY,
//                                    userId.toString()
//                            )
//                            .flatMap(redisPermission -> {
//                                List<String> urlList = JSONObject.parseArray(redisPermission, String.class);
//                                if (!urlList.contains(path)) {
//                                    log.warn("用户没有权限访问：{}", path);
//                                    ServerHttpResponse response = exchange.getResponse();
//                                    return response.writeWith(Mono.just(
//                                            response.bufferFactory()
//                                                    .wrap(JSONObject.toJSONString(
//                                                                    Result
//                                                                            .builder()
//                                                                            .code(401)
//                                                                            .msg("用户没有权限访问")
//                                                                            .build()
//                                                            ).getBytes(StandardCharsets.UTF_8)
//                                                    )
//                                    ));
//                                }
//                                Map<String, Object> userInfos = hMap();
//                                userInfos.put(ConstMapClassNickName.MAP_USERID_KEY, userId);
//                                userInfos.put(ConstMapClassNickName.MAP_USERINFO_KEY, userType);
//                                //将用户信息转化为json字符窜
//                                String userInfoJson = JSONObject.toJSONString(userInfos);
//                                //传递用户信息
//                                ServerWebExchange userInfo = exchange.mutate()
//                                        .request(builder -> builder.header(ConstThreadLocal.USER_INFO_KEY, userInfoJson))
//                                        .build();
//                                return chain.filter(userInfo);
//                            });
                    Map<String, Object> userInfos = hMap();
                                userInfos.put(ConstMapClassNickName.MAP_USERID_KEY, userId);
                                userInfos.put(ConstMapClassNickName.MAP_USERINFO_KEY, userType);
                                userInfos.put(ConstMapClassNickName.MAP_TENANT_ID_RESPONSE_KEY,tenantIdObj);
                                //将用户信息转化为json字符窜
                                String userInfoJson = JSONObject.toJSONString(userInfos);
                                //传递用户信息
                                ServerWebExchange userInfo = exchange.mutate()
                                        .request(builder -> builder.header(ConstHeaderLocals.USER_INFO_KEY, userInfoJson))
                                        .build();
                                return chain.filter(userInfo);
                });
//        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }


    /**
     * 从 HTTP 请求头中提取 JWT Token
     *
     * @param request HTTP 请求对象
     * @return 提取出的 JWT Token，如果不存在或格式不正确则返回 null
     */
    private String extractToken(ServerHttpRequest request) {
        // 从请求头中获取 "Authorization" 字段
        try {
            List<String> header = request.getHeaders().get("Authorization");
//            log.error("==={}",header);
            if (header != null) {
                return header
                        .getFirst()
                        .replace("Bearer ", "")
                        .trim();
            }
        } catch (Exception e) {
            log.error("token异常{}", e.getMessage());
        }
        // 检查请求头是否存在且以 "Bearer " 开头
        // 如果满足条件，则提取 Token（去掉 "Bearer " 前缀）
        return null;
    }

    private Map<String, Object> hMap() {
        return new HashMap<>();
    }


}
