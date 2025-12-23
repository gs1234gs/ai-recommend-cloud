package com.guanshiyun.service.search;

import com.guanshiyun.controller.search.vo.UserSearchSaveVO;
import com.guanshiyun.controller.search.vo.UserSearchVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserSearchService {
    //保存搜索记录
    Mono<BigInteger> save(UserSearchSaveVO userSearchVO);
    //查询搜索记录
    Flux<UserSearchVO> findAll(Integer rows);
}
