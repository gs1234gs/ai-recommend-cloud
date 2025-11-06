package com.guanshiyun.security.handler;

import cn.hutool.json.JSONUtil;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.security.jwt.AssistantJwtUtils;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigInteger;

//@Slf4j
//@Component
//public class RewriteLoginSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Resource
//    RedisUtil redisUtil;

/// /    @Override
/// /    public void onAuthenticationSuccess(HttpServletRequest request,
/// /                                        HttpServletResponse response,
/// /                                        Authentication authentication) throws IOException {
/// /        response.setContentType("application/json;charset=UTF-8");
/// /
/// /        try {
/// /            // 生成 JWT 令牌
/// /            Map<String, Object> map = AssistantJwtUtils.createJwt(authentication);
/// /            if(map != null){
/// /
/// /            String token = (String) map.get(ConstMapClassNickName.MAP_TOKEN_KEY);
/// /            // 获取用户信息，并将用户信息保存到 Redis 中
/// ///                Users userInfo = (Users) map.get("userInfo");
/// ///            log.error("生成 JWT 令牌成功：{}", token);
/// /            int userId = (int) map.get(ConstMapClassNickName.MAP_USERID_KEY);
/// /            //用户权限保存到redis
/// /                redisUtil.hSet(ConstClassNickName.REDIS_AUTHORITY_KEY, userId,
/// /                        map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY));
/// /            //将 token 保存到 Redis 中
/// ///                log.error("redis保存token成功：{}", token);
/// /            redisUtil.hSet(ConstClassNickName.REDIS_TOKEN_KEY, userId, token);
/// ///            log.error("redis保存token成功：{}",token);
/// /            // 构造统一响应
/// /                Map<String,Object> userMap = new HashMap<>();
/// /                userMap.put(ConstMapClassNickName.MAP_USERID_RESPONSE_KEY,userId);
/// /                userMap.put(ConstMapClassNickName.MAP_TOKEN_RESPONSE_KEY,token);
/// /            Result result = Result
/// /                    .builder()
/// /                    .status(200)
/// /                    .msg("登录成功")
/// /                    .data(userMap)  // 返回 token 而非 principal
/// /                    .build();
/// /            response.getWriter().write(JSONUtil.toJsonStr(result));
/// /            return;
/// /            }
/// /        } catch (Exception e) {
/// /            log.error("处理器发生异常：{}", e.getMessage());
/// /        }
/// /        // 返回登录失败
/// /        response.getWriter().write(
/// /
/// /                JSONUtil.toJsonStr(
/// /                        Result
/// /                                .builder()
/// /                                .status(401)
/// /                                .msg("登录成功处理器发生异常")
/// /                                .build())
/// /        );
/// /
/// /    }
/// /
/// /    public void onAuthenticationError(HttpServletResponse response) throws IOException {
/// /
/// /        response.setContentType("application/json;charset=UTF-8");
/// /        response.getWriter().write(
/// /                JSONUtil.toJsonStr(
/// /                        Result
/// /                                .builder()
/// /                                .status(401)
/// /                                .msg("用户名或密码错误")
/// /                                .build())
/// /        );
/// /    }
//}
@Slf4j
@Component
@RequiredArgsConstructor
public class RewriteLoginSuccessHandler {

    private final ReactiveRedisUtil redisUtil;
    public Mono<Result> onAuthenticationSuccess(Authentication authentication) {

        try {
            return AssistantJwtUtils.createJwt(authentication)
                    .flatMap(map -> {
                        String token = (String) map.get(ConstMapClassNickName.MAP_TOKEN_KEY);
//                        log.info("保存token：{}", token);
                        BigInteger id = (BigInteger) map.get(ConstMapClassNickName.MAP_USERID_KEY);
                        Mono<Boolean> saveToken = redisUtil.hSet(
                                        ConstClassNickName.REDIS_TOKEN_KEY,
                                        id.toString(),
                                        JSONUtil.toJsonStr(token)
                                )
                                .then(redisUtil.expire(ConstClassNickName.REDIS_TOKEN_KEY,
                                        60));
//                 List<SimpleGrantedAuthority>  auth1 =(List<SimpleGrantedAuthority>) map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY);
//                 List<String> auth = auth1.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
                        Object auth = map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY);
//                    System.out.println("保存权限：{}"+auth);
//                        log.info("保存权限：{}", auth);
                        Mono<Boolean> saveAuthority = redisUtil.hSet(
                                        ConstClassNickName.REDIS_AUTHORITY_KEY,
                                        id,
                                        JSONUtil.toJsonStr(auth))
                                .then(redisUtil.expire(
                                        ConstClassNickName.REDIS_AUTHORITY_KEY,
                                        60));
//                    System.out.println("保存权限：{}"+map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY));
//                    redisUtil.hGet(ConstClassNickName.REDIS_AUTHORITY_KEY, id)
//                            .switchIfEmpty(Mono.just("没有权限"))
//                            .map(json -> JSONUtil.toList((String) json, String.class)) // ✅ 转换为 List<String>
//                            .subscribe(perm->log.info("拿到权限：{}",perm));
                        return Mono.zip(saveToken, saveAuthority)
                                .flatMap(tuple -> {
//                                    Map<String, Object> userMap = new HashMap<>();
//                                    userMap.put(ConstMapClassNickName.MAP_USERID_RESPONSE_KEY, id);
//                                    userMap.put(ConstMapClassNickName.MAP_TOKEN_RESPONSE_KEY, token);
                                    return Mono.just(Result
                                            .builder()
                                            .code(HttpCodeConst.OK)
                                            .msg("登录成功")
                                            .data(token)
                                            .build());
                                });
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
