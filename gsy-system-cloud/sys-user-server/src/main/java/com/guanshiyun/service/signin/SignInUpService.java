package com.guanshiyun.service.signin;

import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.signinpojo.SignUser;
import com.guanshiyun.userpojo.SysUser;
import reactor.core.publisher.Mono;

public interface SignInUpService {

    //登陆
    Mono<SignUser> signIn(String username);

    //注册
    Mono<ResultT<String>> signUp(SysUser signUser);
}
