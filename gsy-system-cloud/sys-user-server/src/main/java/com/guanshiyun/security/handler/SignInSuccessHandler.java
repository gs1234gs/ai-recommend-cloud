package com.guanshiyun.security.handler;

import cn.hutool.json.JSONUtil;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.security.jwt.AssistantJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;




@Slf4j
@Component
@RequiredArgsConstructor
public class SignInSuccessHandler {

    private final ReactiveRedisUtil redisUtil;
    @SneakyThrows
    public Mono<ResultT< String>> onAuthenticationSuccess(Authentication authentication) {
            return AssistantJwtUtils.createJwt(authentication)
                    .flatMap(map -> {
                        String token = (String) map.get(ConstMapClassNickName.MAP_TOKEN_KEY);
//                        log.info("保存token：{}", token);
                        Long id = (Long) map.get(ConstMapClassNickName.MAP_USERID_KEY);
                        Mono<Boolean> saveToken = redisUtil.hSet(
                                        ConstClassNickName.REDIS_TOKEN_KEY,
                                        id.toString(),
                                        JSONUtil.toJsonStr(token)
                                )
                                .then(redisUtil.expire(ConstClassNickName.REDIS_TOKEN_KEY,
                                        ConstNumber.INT_THOUSAND));
//                 List<SimpleGrantedAuthority>  auth1 =(List<SimpleGrantedAuthority>) map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY);
//                 List<String> auth = auth1.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
                        Object auth = map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY);
//                    System.out.println("保存权限：{}"+auth);
//                        log.info("保存权限：{}", auth);
                        Mono<Boolean> saveAuthority = redisUtil.hSet(
                                        ConstClassNickName.REDIS_AUTHORITY_KEY,
                                        String.valueOf(id),
                                        JSONUtil.toJsonStr(auth))
                                .then(redisUtil.expire(
                                        ConstClassNickName.REDIS_AUTHORITY_KEY,
                                        ConstNumber.INT_THOUSAND));
//                    System.out.println("保存权限：{}"+map.get(ConstMapClassNickName.MAP_AUTHORITY_KEY));
//                    redisUtil.hGet(ConstClassNickName.REDIS_AUTHORITY_KEY, id)
//                            .switchIfEmpty(Mono.just("没有权限"))
//                            .map(json -> JSONUtil.toList((String) json, String.class)) // 转换为 List<String>
//                            .subscribe(perm->log.info("拿到权限：{}",perm));
                        return Mono.zip(saveToken, saveAuthority)
                                .flatMap(tuple -> {
//                                    Map<String, Object> userMap = new HashMap<>();
//                                    userMap.put(ConstMapClassNickName.MAP_USERID_RESPONSE_KEY, id);
//                                    userMap.put(ConstMapClassNickName.MAP_TOKEN_RESPONSE_KEY, token);
                                    return Mono.just(ResultT
                                            .<String>builder()
                                            .code(HttpStatus.OK.value())
                                            .msg("登录成功")
                                            .data(token)
                                            .build());
                                });
                    })
                    .onErrorResume(throwable -> {
                        return Mono.just(ResultT.<String>builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .msg("登录失败")
                                .data("")
                                .build());
                    });


    }
}
