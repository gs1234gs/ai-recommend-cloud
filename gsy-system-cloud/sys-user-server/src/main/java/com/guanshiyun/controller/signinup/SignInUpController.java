package com.guanshiyun.controller.signinup;


import com.guanshiyun.emailutil.QQEmailUtil;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.security.handler.SignInSuccessHandler;
import com.guanshiyun.security.reponse.CustomReactiveAuthenticationManager;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.signinpojo.SignUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/signInUp")
@RequiredArgsConstructor
public class SignInUpController {
    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;
    private final SignInSuccessHandler signInSuccessHandler; // Changed from private final SignInSuccessHandler signInSuccessHandler;
    private final SignInUpService signInUpService;

    @PostMapping("/signIn")
    public Mono<ResultT<String>> signIn(@RequestBody SignUser signUser) {
//        log.info("用户名：{}", signUser);
        String username = signUser.getUsername();
        String password = signUser.getPassword();
        String code = signUser.getCode();
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);
        //固定用户，不走验证码
        if(getFixedUsers().contains(username)){
            return customReactiveAuthenticationManager.authenticate(authRequest)
                    .flatMap(signInSuccessHandler::onAuthenticationSuccess);
        }
        return customReactiveAuthenticationManager.checkCode(username, code)
                .flatMap(check -> customReactiveAuthenticationManager.authenticate(authRequest))
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
    public Mono<ResultT<Boolean>> signUp(@RequestBody SignUser signUpUser) {
        String username = signUpUser.getUsername();
        if(QQEmailUtil.isNotValidEmail(username)){
            return Mono.error(new Throwable("请输入正确的邮箱格式"));
        }
        //格式正确，获取校验码校验
        String code = signUpUser.getCode();
        return customReactiveAuthenticationManager.checkCode(username, code)
                .flatMap(check -> {
                    return signInUpService.signUp(signUpUser)
                            .map(up -> {
                                if (up) {
                                    return ResultT.<Boolean>builder()
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
                });

    }

    //获取验证码
    @PostMapping("/findCode")
    public Mono<ResultT<String>> findCode(@RequestBody SignUser signUser) {
        return customReactiveAuthenticationManager.sendVerificationCode(signUser.getUsername())
                .map(msg -> ResultT.<String>builder()
                        .code(HttpStatus.OK.value())
                        .msg(msg)
                        .build());
    }

    //固定用户
    public List<String> getFixedUsers() {
        return List.of("admin",
                "guanshiyun",
                "xvchaliu",
                "test",
                "15287919470",
                "152872185729",
                "152872185711",
                "15385699875",
                "15385699878",
                "15287945678");
    }
}
