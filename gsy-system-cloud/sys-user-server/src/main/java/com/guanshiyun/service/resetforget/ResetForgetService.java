package com.guanshiyun.service.resetforget;

import com.guanshiyun.pojo.signreqpojo.SignRequestUser;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

public interface ResetForgetService {

    Mono<ResultT<Object>> resetForget(SignRequestUser signRequestUser);
}
