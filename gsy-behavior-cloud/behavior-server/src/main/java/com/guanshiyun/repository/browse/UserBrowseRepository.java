package com.guanshiyun.repository.browse;

import com.guanshiyun.browse.UserBrowse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface UserBrowseRepository extends ReactiveCrudRepository<UserBrowse, BigInteger> {
    //根据参数获取最近总记录条数

    @Query("select * from user_browse where del_flag = 0 and creator= :creator order by id desc limit :rows")
    Flux<UserBrowse> findAll(@Param("rows")  Integer rows,@Param("creator") BigInteger creator);
    //无用户id
    @Query("select * from user_browse where del_flag = 0 order by id desc limit :rows")
    Flux<UserBrowse> findAll(@Param("rows")  Integer rows);
}
