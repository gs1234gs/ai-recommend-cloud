package com.guanshiyun.repository.tag;

import com.guanshiyun.tag.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.math.BigInteger;

public interface TagRepository extends R2dbcRepository<Tag, BigInteger> {

}
