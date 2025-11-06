package com.guanshiyun.rpc.behaviorapi.click;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserClickServiceApi {
    Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(@RequestParam(required = false) Integer rows);
}
