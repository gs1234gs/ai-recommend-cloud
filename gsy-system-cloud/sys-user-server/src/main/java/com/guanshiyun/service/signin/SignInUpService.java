package com.guanshiyun.service.signin;

import com.guanshiyun.signinpojo.SignUser;
import reactor.core.publisher.Mono;

public interface SignInUpService {

    //登陆
    Mono<SignUser> signIn(String username);

    //注册
    Mono<Boolean> signUp(SignUser signUser);


}
