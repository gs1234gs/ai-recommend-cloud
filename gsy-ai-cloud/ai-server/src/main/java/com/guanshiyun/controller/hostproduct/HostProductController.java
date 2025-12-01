package com.guanshiyun.controller.hostproduct;

import com.alibaba.nacos.api.model.v2.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hostProduct")
public class HostProductController {
    @GetMapping("/hostData")
    public Mono<Result<List<BigInteger>>> hostData(){
        return Mono.just(Result.success(null));
    }
}
