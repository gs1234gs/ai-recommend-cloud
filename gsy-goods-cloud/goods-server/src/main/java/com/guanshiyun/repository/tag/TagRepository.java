package com.guanshiyun.repository.tag;

import com.guanshiyun.tag.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;



public interface TagRepository extends R2dbcRepository<Tag, Long> {

}
