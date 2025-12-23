package com.guanshiyun.rpc.behaviorapi.collect;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.CollectProfileApi;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserCollectServiceApi {
    Mono<ResultT<List<CollectProfileApi>>> findUserCollectRecord(@RequestParam(required = false) Integer rows);
}
