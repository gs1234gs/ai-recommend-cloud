package com.guanshiyun.service.category;

import com.guanshiyun.category.Category;
import com.guanshiyun.controller.category.vo.CategorySaveVO;
import com.guanshiyun.controller.category.vo.CategoryVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface CategoryService {
    //添加类型
    Mono<Long> save(CategorySaveVO categorySaveVO);

    Mono<Void> deleteById(Long id);

    Mono<Category> fndById(Long id);

    Mono<PageResultT<List<CategoryVO>>> findAllByPage( RequestPage<CategoryVO> requestPage);

    Mono<List<CategoryVO>> findAll();

    Flux<CategoryVO> findByProductId(Long productId);
}
