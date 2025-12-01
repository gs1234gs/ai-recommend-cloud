package com.guanshiyun.rpc.behaviorapi.click;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.behaviorapi.browse.vo.UserBrowseVOApi;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface UserClickServiceApi {
    Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows);
    Mono<ResultT<List<UserBrowseVOApi>>> findUserBrowseRecord(Integer rows, BigInteger userId);
}
