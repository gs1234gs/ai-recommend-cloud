package com.guanshiyun.service.collect;

import com.guanshiyun.controller.collect.vo.UserCollectSaveVO;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserCollectService {
    //保存收藏记录
    Mono<BigInteger> save(UserCollectSaveVO userCollectSaveVO);
    //查询收藏记录
    Flux<UserCollectVO> findAll(Integer rows);
}
