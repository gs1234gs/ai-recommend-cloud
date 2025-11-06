package com.guanshiyun.service.collect;

import com.guanshiyun.controller.collect.vo.UserCollectVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserCollectService {
    //保存收藏记录
    Mono<BigInteger> save(UserCollectVO userCollectVO);
    //查询收藏记录
    Flux<UserCollectVO> findAll(Integer rows);
}
