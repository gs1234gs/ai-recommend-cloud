package com.guanshiyun.service.resetforget;

import com.guanshiyun.pojo.signreqpojo.SignRequestUser;
import com.guanshiyun.responsepojo.Result;
import reactor.core.publisher.Mono;

public interface ResetForgetService {

    Mono<Result> resetForget(SignRequestUser signRequestUser);
}
