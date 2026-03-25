package com.guanshiyun.service.collect;

import com.guanshiyun.controller.collect.vo.UserCollectSaveVO;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface UserCollectService {
    //保存收藏记录
    Mono<Long> save(UserCollectSaveVO userCollectSaveVO);
    //查询收藏记录
    Flux<UserCollectVO> findAll(Integer rows);

    Mono<Void> deleteById(Long id);
    //分页查询
    Mono<PageResultT<List<UserCollectVO>>> findByPage(Integer pageNum, Integer pageSize);
}
