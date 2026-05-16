package com.guanshiyun.service.QQEmailVerificationCode;

import com.guanshiyun.rpc.qqCode.QQCode;
import reactor.core.publisher.Mono;

public interface QQEmailCodeService {
    //发送验证码
    Mono<Boolean> sendQQEmailCode(QQCode qqCode);
}
