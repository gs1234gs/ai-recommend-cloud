package com.guanshiyun.repository.category;

import com.guanshiyun.category.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

}
