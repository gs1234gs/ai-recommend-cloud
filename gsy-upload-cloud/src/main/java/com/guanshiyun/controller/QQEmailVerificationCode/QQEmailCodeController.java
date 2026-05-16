package com.guanshiyun.controller.QQEmailVerificationCode;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.qqCode.QQCode;
import com.guanshiyun.service.QQEmailVerificationCode.QQEmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 短信验证码服务相关控制器
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/qqEmailCode")
public class QQEmailCodeController {
    private final QQEmailCodeService qqEmailCodeService;

    @PostMapping("/sendQQEmailCode")
    public Mono<ResultT<Boolean>> sendQQEmailCode(@RequestBody QQCode qqCode) {
       return qqEmailCodeService.sendQQEmailCode(qqCode)
               .map(ResultT::success);
    }

}
