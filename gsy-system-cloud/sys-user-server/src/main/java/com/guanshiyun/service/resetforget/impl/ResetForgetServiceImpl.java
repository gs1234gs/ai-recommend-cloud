package com.guanshiyun.service.resetforget.impl;

import com.guanshiyun.pojo.signreqpojo.SignRequestUser;
import com.guanshiyun.repository.resetforget.ResetForgetRepository;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.security.reponse.CustomReactiveAuthenticationManager;
import com.guanshiyun.service.resetforget.ResetForgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResetForgetServiceImpl implements ResetForgetService {
    private final ResetForgetRepository resetForgetRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Override
    public Mono<ResultT<Object>> resetForget(SignRequestUser signRequestUser) {
        //先获取验证码校验
        String username = signRequestUser.getUsername();
        String code = signRequestUser.getCode();
       return customReactiveAuthenticationManager.checkCode(username, code)
                .flatMap(check -> {
                    return resetForgetRepository.findByUsername(signRequestUser.getUsername())
                            .flatMap(resetUser -> {
                                resetUser.setPassword(passwordEncoder.encode(signRequestUser.getPassword()));
                                return resetForgetRepository.save(resetUser)
                                        .map(user -> ResultT.success("修改密码成功"));
                            })
                            .switchIfEmpty(Mono.just(ResultT.error("用户不存在")))
                            .onErrorResume(e->Mono.just(ResultT.error("修改密码失败")));
                });

    }
}
