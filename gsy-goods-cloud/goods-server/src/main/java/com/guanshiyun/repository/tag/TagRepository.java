package com.guanshiyun.repository.tag;

import com.guanshiyun.tag.Tag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface TagRepository extends ReactiveCrudRepository<Tag, BigInteger> {

}
