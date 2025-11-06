package com.guanshiyun.repository.click;

import com.guanshiyun.click.UserClick;
import com.guanshiyun.controller.click.vo.UserClickVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface UserClickRepository extends ReactiveCrudRepository<UserClick, BigInteger> {

    //根据参数获取最新数目记录列表
    @Query("select * from user_click where del_flag = 0 order by id desc limit :rows")
    Flux<UserClickVO> findAll(@Param("rows") Integer rows);
    //根据参数获取最新数目记录列表,加上creator
    @Query("select * from user_click where del_flag = 0 and creator= :creator order by id desc limit :rows")
   Flux<UserClickVO> findAll(@Param("rows") Integer rows,@Param("creator") BigInteger creator);
}
