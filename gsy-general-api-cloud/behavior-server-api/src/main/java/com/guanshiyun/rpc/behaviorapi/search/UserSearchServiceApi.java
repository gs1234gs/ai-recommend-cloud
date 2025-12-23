package com.guanshiyun.rpc.behaviorapi.search;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.SearchContentApi;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface UserSearchServiceApi {
    // 查询用户搜索记录
    Mono<ResultT<List<SearchContentApi>>> findUserSearchRecord(Integer rows);
    //保存搜索记录
    Mono<ResultT<BigInteger>> saveUserSearchRecord(SearchContentApi searchContentApi);
}
