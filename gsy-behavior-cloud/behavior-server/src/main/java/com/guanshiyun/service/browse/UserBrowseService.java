package com.guanshiyun.service.browse;

import com.guanshiyun.browse.UserBrowse;
import com.guanshiyun.controller.browse.vo.UserBrowseVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserBrowseService {
    //保存浏览记录
    Mono<BigInteger> save(UserBrowseVO userBrowseVO);
    //更新浏览记录
    Mono<BigInteger> update(UserBrowseVO userBrowseVO);
    //获取浏览记录
    Flux<UserBrowseVO> findAll(Integer rows);
    //游标查询
    Flux<UserBrowseVO> findAllByCursor(RequestCursorPage<UserBrowse> cursorPage);
}
