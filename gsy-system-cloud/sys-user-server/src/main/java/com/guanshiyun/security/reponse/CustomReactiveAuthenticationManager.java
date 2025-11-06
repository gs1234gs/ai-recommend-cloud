package com.guanshiyun.security.reponse;

import com.guanshiyun.service.signin.SignInUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final SignInUpService signInUpService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (!(authentication instanceof UsernamePasswordAuthenticationToken auth)) {
            return Mono.error(new IllegalArgumentException("无效用户"));
        }

        String username = auth.getName();
//        log.error("用户名：{}", username);
        String password = (String) auth.getCredentials();
//        //获取用户信息
//        SignUser signUser = auth.getPrincipal() instanceof SignUser ? (SignUser) auth.getPrincipal() : null;
        // 实现认证逻辑
        //认证密码
//        assert signUser != null;
//        if(!passwordEncoder.matches(password, signUser.getPassword())){
//            return Mono.error(new BadCredentialsException("密码错误"));
//        }
        //认证成功
        return signInUpService.signIn(username)
                .switchIfEmpty(Mono.error(new BadCredentialsException("用户不存在")))
                .flatMap(
                        user -> Mono.fromCallable(() -> {
                            log.info("用户名：{}", username);
                            log.info("密码：{}", password);
                            log.info("用户信息：{}", user);
                            log.info("密码匹配：{}", passwordEncoder.matches(password, user.getPassword()));
                                   return passwordEncoder.matches(password, user.getPassword());
                                })
                            .filter(Boolean::booleanValue)
                            .switchIfEmpty(Mono.error(new BadCredentialsException("用户名或密码错误")))
                            .map(matches -> new UsernamePasswordAuthenticationToken(
                                    user,
                                    password,
                                    Collections.emptyList()
                            ))
                );
    }
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
        System.out.println(bCryptPasswordEncoder.matches(

                "123456",
                "$2a$10$HeOf0/bbu3etDUEjwTs6Eu.rR/NX.lYjir66sGhxB5E8m4GoXMDCi"
        ));

    }
}
