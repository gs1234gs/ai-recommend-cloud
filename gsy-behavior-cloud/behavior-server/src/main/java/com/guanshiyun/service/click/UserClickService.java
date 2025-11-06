package com.guanshiyun.service.click;

import com.guanshiyun.controller.click.vo.UserClickVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserClickService {
   Mono<BigInteger> save(UserClickVO userClickVO);
   // 查询点击记录
   Flux<UserClickVO> findAll(Integer rows);
}
