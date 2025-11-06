package com.guanshiyun.security.jwt;


import com.guanshiyun.consts.ConstMapClassNickName;
import com.guanshiyun.signinpojo.SignUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AssistantJwtUtils {
    public static Mono<Map<String, Object>> createJwt(Authentication authentication) throws IOException {
        // 提取用户信息（UserDetailsReWrite）
        Object principal = authentication.getPrincipal();
        // 判断 principal 是否为 UserDetailsReWrite
        if(principal instanceof SignUser userDetailsReWrite){
            Map<String, Object> map = new HashMap<>();
            map.put(ConstMapClassNickName.MAP_USERID_KEY, userDetailsReWrite.getSysUser().getId());
            map.put(ConstMapClassNickName.MAP_USERNAME_KEY, userDetailsReWrite.getSysUser().getUsername());
            map.put(ConstMapClassNickName.MAP_USERTYPE_KEY, userDetailsReWrite.getSysUser().getType());
            // 生成访问令牌（Access Token）和刷新令牌（Refresh Token）
            // 假设 JwtUtils 是一个工具类，用于生成 JWT 令牌
            String token = JwtUtils.genToken(map, userDetailsReWrite.getSysUser().getId().toString());
            map.put(ConstMapClassNickName.MAP_AUTHORITY_KEY, userDetailsReWrite.getAuthorities());
            // 返回包含令牌的成功响应"accessToken", token
//            log.info("springSecurity集成成功一半：{}", token);
            map.put(ConstMapClassNickName.MAP_TOKEN_KEY, token);
            map.put(ConstMapClassNickName.MAP_USERINFO_KEY, userDetailsReWrite.getSysUser());
            return Mono.just(map);
        }
        return Mono.empty();
    }
}
