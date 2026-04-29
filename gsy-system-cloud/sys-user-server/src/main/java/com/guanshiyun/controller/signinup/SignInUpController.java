package com.guanshiyun.controller.signinup;


import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.security.handler.SignInSuccessHandler;
import com.guanshiyun.security.reponse.CustomReactiveAuthenticationManager;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final SignInSuccessHandler signInSuccessHandler; // Changed from private final SignInSuccessHandler signInSuccessHandler;
    private final SignInUpService signInUpService;

    @PostMapping("/signIn")
    public Mono<ResultT< String>> signIn(@RequestBody SysUser signUser) {
        log.info("用户名：{}", signUser);
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(signUser.getUsername(), signUser.getPassword());
                    return customReactiveAuthenticationManager.authenticate(authRequest)
                            .flatMap(signInSuccessHandler::onAuthenticationSuccess);
//                            .onErrorResume(throwable -> {
//                                log.error("登录失败：", throwable);
//                              return   Mono.just(
//                                        ResultT.<String>builder()
//                                                 .code(HttpStatus.FORBIDDEN.value())
//                                                .msg("登录失败，用户名或密码错误！")
//                                                .build()
//                                );
//                            });
    }

    @PostMapping("/signUp")
    public Mono<ResultT<Boolean>> signUp(@RequestBody SysUser signUpUser) {
        return signInUpService.signUp(signUpUser)
                .map(up->{
                    if(up){
                      return  ResultT.<Boolean>builder()
                                .code(HttpStatus.OK.value())
                                .msg("注册成功")
                                .data(up)
                                .build();
                    }
                    return ResultT.<Boolean>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .msg("用户已存在,请重新注册")
                                .data(up)
                                .build();
                });
    }
}
