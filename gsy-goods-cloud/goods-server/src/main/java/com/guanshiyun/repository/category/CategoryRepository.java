package com.guanshiyun.repository.category;

import com.guanshiyun.category.Category;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


import java.util.List;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    @Query("select * from category where id in (:categoryIdList)")
    Flux<Category> findByIdIn(List<Long> categoryIdList);
}
