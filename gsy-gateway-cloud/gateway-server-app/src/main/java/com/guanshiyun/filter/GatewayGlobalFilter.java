package com.guanshiyun.filter;


import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.aliyun.oss.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.consts.ConstHeaderLocals;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.consts.PublicEndpoints;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    private final ObjectMapper objectMapper;

    private final String IP_KEY = "ip";
    private final String COUNT_KEY = "count";
    private final String TIME_KEY = "time";

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 获取客户端IP
//        String ip = getClientIp(request);
//        String path = request.getPath().value();
//        String ipPath = ip + path;
//        return requestLimit(exchange, chain, ipPath, ip, request);
        return checkToken(exchange, chain, request);
    }

    //限流
    //
    private @NotNull Mono<Void> requestLimit(ServerWebExchange exchange, GatewayFilterChain chain, String ipPath, String ip, ServerHttpRequest request) {
        return reactiveRedisUtil
                .hGet(ConstClassNickName.REDIS_IP_KEY, ipPath)
                // 1. 如果 Redis 中没有该 IP 的记录，初始化一条新数据
                .switchIfEmpty(Mono.defer(() -> {
                    //访问时间
                    LocalDateTime now = LocalDateTime.now();

                    Map<String, Object> mapIp = new HashMap<>();
                    mapIp.put(IP_KEY, ip);
                    mapIp.put(TIME_KEY, now);
                    mapIp.put(COUNT_KEY, 0);
                    // 序列化为 JSON 并存入 Redis，最后返回一个包含 JSON 字符串的 Mono
                    try {
                        String json = objectMapper.writeValueAsString(mapIp);
                        // 核心：使用 .thenReturn(json) 保证 switchIfEmpty 返回的依然是 Mono<String>
                        return reactiveRedisUtil.hSet(ConstClassNickName.REDIS_IP_KEY, ipPath, json)
                                .flatMap(aVoid -> reactiveRedisUtil.expire(ConstClassNickName.REDIS_IP_KEY, 60))
                                .thenReturn(json);
                    } catch (JsonProcessingException e) {
                        throw Exceptions.propagate(e);
                    }
                }))
                .flatMap(json -> {
                    try {
                        Map<String, Object> mapIp = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
                        });
                        Object timeObj = mapIp.get(TIME_KEY);
                        LocalDateTime time;
                        if (timeObj instanceof LocalDateTime) {
                            // 如果已经是 LocalDateTime 对象（极少数情况），直接强转
                            time = (LocalDateTime) timeObj;
                        } else {
                            // 如果是字符串（从Redis读出来的情况），手动解析
                            time = LocalDateTime.parse(timeObj.toString());
                        }
                        LocalDateTime now = LocalDateTime.now();
                        Integer count = (Integer) mapIp.get(COUNT_KEY);
                        // 2. 安检与更新
                        if (time.plusMinutes(1).isAfter(now)) {
                            // 还在1分钟内
                            if (count >= 10) {
                                // 超过限制，直接拦截！
                                return Mono.error(new RuntimeException("访问次数超过10次，请稍后再试"));
                            }
                            // 没超限，次数加 1（这里加完就是最终要存的次数）
                            mapIp.put(COUNT_KEY, count + 1);
                        }
                        String value = null;
                        try {
                            value = objectMapper.writeValueAsString(mapIp);
                        } catch (JsonProcessingException e) {
                            return Mono.error(new RuntimeException(e));
                        }
                        return reactiveRedisUtil.hSet(ConstClassNickName.REDIS_IP_KEY, ipPath, value)
                                .flatMap(aVoid -> {
                                    try {
                                        return checkToken(exchange, chain, request);
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new RuntimeException(e));
                                    }
                                })
                                .onErrorResume(Mono::error);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
    }

    //校验token
    private Mono<Void> checkToken(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request) throws JsonProcessingException {
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
                                    .wrap(
                                            objectMapper.writeValueAsBytes(
                                                    ResultT
                                                            .builder()
                                                            .code(HttpStatus.UNAUTHORIZED.value())
                                                            .msg("未登陆")
                                                            .build()
                                            )
                                    )
                    )
            );
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
                            .wrap(
                                    objectMapper.writeValueAsBytes(
                                            ResultT
                                                    .builder()
                                                    .code(HttpStatus.UNAUTHORIZED.value())
                                                    .msg("登陆已经过期")
                                                    .build()
                                    )
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
                .defaultIfEmpty("")
                .flatMap(redisToken -> {
                    if (StringUtil.isNullOrEmpty(redisToken)) {
                        log.error("登陆已经过期: {}", userId);
                        ServerHttpResponse response = exchange.getResponse();
                        try {
                            return response.writeWith(Mono.just(
                                    response.bufferFactory()
                                            .wrap(
                                                    objectMapper.writeValueAsBytes(
                                                            ResultT
                                                                    .builder()
                                                                    .code(HttpStatus.UNAUTHORIZED.value())
                                                                    .msg("登陆已经过期")
                                                                    .build()
                                                    )
                                            )
                            ));
                        } catch (JsonProcessingException e) {
                            return Mono.error(new RuntimeException(e));
                        }
                    }
                    try {
                        if (!token.equals(redisToken)) {
                            log.error("Token与Redis中不一致，userId: {}", userId);
                            ServerHttpResponse response = exchange.getResponse();
                            return response.writeWith(Mono.just(
                                    response.bufferFactory()
                                            .wrap(
                                                    objectMapper.writeValueAsBytes(
                                                            ResultT
                                                                    .builder()
                                                                    .code(HttpStatus.UNAUTHORIZED.value())
                                                                    .msg("登陆已经过期")
                                                                    .build()
                                                    )
                                            )
                            ));
                        }
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException(e));
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
//                                                                    ResultT
//                                                                            .builder()
//                                                                            .code(HttpStatus.UNAUTHORIZED.value())
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
                    Map<String, Object> userInfos = new HashMap<>();
                    userInfos.put(ConstMapClassNickName.MAP_USERID_KEY, userId);
                    userInfos.put(ConstMapClassNickName.MAP_USERINFO_KEY, userType);
                    userInfos.put(ConstMapClassNickName.MAP_TENANT_ID_RESPONSE_KEY, tenantIdObj);
                    //将用户信息转化为json字符窜
                    String userInfoJson = null;
                    try {
                        userInfoJson = objectMapper.writeValueAsString(userInfos);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException(e));
                    }
                    //传递用户信息
                    String finalUserInfoJson = userInfoJson;
                    ServerWebExchange userInfo = exchange.mutate()
                            .request(builder -> builder.header(ConstHeaderLocals.USER_INFO_KEY, finalUserInfoJson))
                            .build();
                    return chain.filter(userInfo);
                });
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

    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }

        ip = request.getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

}
