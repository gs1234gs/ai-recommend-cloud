package com.guanshiyun.service.click;

import com.guanshiyun.controller.click.vo.UserClickSaveVO;
import com.guanshiyun.controller.click.vo.UserClickVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface UserClickService {
   Mono<Long> save(UserClickSaveVO userClickSaveVO);
   // 查询点击记录
   Flux<UserClickVO> findAll(Integer rows);
}
