package com.guanshiyun.controller.logout;

import com.guanshiyun.pojo.signreqpojo.SignRequestUser;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.resetforget.ResetForgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reset")
public class ResetForgetController {

    private final ResetForgetService resetForgetService;


    @PostMapping("/forget")
    public Mono<ResultT<Object>> resetForget(@RequestBody SignRequestUser signRequestUser){
        return resetForgetService.resetForget(signRequestUser);
    }


}
