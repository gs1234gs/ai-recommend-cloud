package com.guanshiyun.rpc.behaviorapi.browse;


import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import reactor.core.publisher.Mono;

import java.util.List;


public interface UserBrowseServiceApi {
    Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord( Integer rows);
}
