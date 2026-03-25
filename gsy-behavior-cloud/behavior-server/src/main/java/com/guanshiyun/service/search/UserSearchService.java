package com.guanshiyun.service.search;

import com.guanshiyun.controller.search.vo.UserSearchSaveVO;
import com.guanshiyun.controller.search.vo.UserSearchVO;
import reactor.core.publisher.Mono;


import java.util.List;

public interface UserSearchService {
    //保存搜索记录
    Mono<Long> save(UserSearchSaveVO userSearchVO);
    //查询搜索记录
    Mono<List<UserSearchVO>> findAll(Integer rows);
}
