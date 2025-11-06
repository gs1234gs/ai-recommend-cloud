package com.guanshiyun.repository.search;

import com.guanshiyun.controller.search.vo.UserSearchVO;
import com.guanshiyun.search.UserSearch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface UserSearchRepository extends ReactiveCrudRepository<UserSearch, BigInteger> {
    //根据参数查询最新记录总数
    @Query("SELECT * FROM user_search ORDER BY id DESC LIMIT :rows")
    Flux<UserSearchVO> findAll(@Param("rows") Integer rows);

    @Query("SELECT * FROM user_search WHERE user_id = :creator ORDER BY id DESC LIMIT :rows")
    Flux<UserSearchVO> findAll(@Param("rows") Integer rows, @Param("creator") BigInteger creator);
}
