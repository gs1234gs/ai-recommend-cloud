package com.guanshiyun.controller.signinup;


import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.security.handler.RewriteLoginSuccessHandler;
import com.guanshiyun.security.reponse.CustomReactiveAuthenticationManager;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/signInUp")
@RequiredArgsConstructor
public class SignInUpController {
    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;
    private final RewriteLoginSuccessHandler rewriteLoginSuccessHandler;
    private final SignInUpService signInUpService;

    @PostMapping("/signIn")
    public Mono<Result> signIn(@RequestBody SysUser signUser) {
        log.info("用户名：{}", signUser);
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(signUser.getUsername(), signUser.getPassword());
                    return customReactiveAuthenticationManager.authenticate(authRequest)
                            .flatMap(rewriteLoginSuccessHandler::onAuthenticationSuccess)
                            .onErrorResume(throwable -> {
                                log.error("登录失败：", throwable);
                              return   Mono.just(
                                        Result.builder()
                                                .code(HttpCodeConst.FORBIDDEN)
                                                .msg("登录失败，用户名或密码错误！")
                                                .data(throwable)
                                                .build()
                                );
                            });
    }

    @PostMapping("/signUp")
    public Mono<Result> signUp(@RequestBody SysUser signUpUser) {
        return signInUpService.signUp(signUpUser);
    }
}
