package com.guanshiyun.rpc.qq;


import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.qqCode.QQCode;
import reactor.core.publisher.Mono;

public interface QQEmailApi {
    /**
     * 发送验证码
     * */
    Mono<ResultT<Boolean>> sendVerificationCode(QQCode qqCode);
}
