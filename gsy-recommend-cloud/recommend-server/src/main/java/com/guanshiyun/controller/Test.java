package com.guanshiyun.controller;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.UserBrowseServiceApi;
import com.guanshiyun.rpc.profile.BrowseProfileApi;
import com.guanshiyun.utils.WebContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class Test {
    private final UserBrowseServiceApi userBrowseServiceApi;
    private final WebContextUtils webContextUtils;

    @GetMapping("/test")
    public Mono<ResultT<List<BrowseProfileApi>>> test() {
        return webContextUtils.withUserContextMono(() ->
                userBrowseServiceApi.findUserBrowseRecord(10)
        );
    }
}
