package com.guanshiyun.repository.collect;

import com.guanshiyun.collect.UserCollect;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface UserCollectRepository extends ReactiveCrudRepository<UserCollect, BigInteger> {
    @Query("SELECT * FROM user_collect WHERE creator = :useId ORDER BY id DESC LIMIT :rows")
    Flux<UserCollect> findAll(@Param("rows") Integer rows,@Param("useId") BigInteger useId);

    @Query("SELECT * FROM user_collect ORDER BY id DESC LIMIT :rows")
    Flux<UserCollect> findAll(@Param("rows") Integer rows);
}
