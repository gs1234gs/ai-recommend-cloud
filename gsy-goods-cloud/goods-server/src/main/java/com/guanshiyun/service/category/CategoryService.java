package com.guanshiyun.service.category;

import com.guanshiyun.category.Category;
import com.guanshiyun.controller.category.vo.CategorySaveVO;
import com.guanshiyun.controller.category.vo.CategoryVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface CategoryService {
    //添加类型
    Mono<BigInteger> save(CategorySaveVO categorySaveVO);

    Mono<Void> deleteById(BigInteger id);

    Mono<Category> fndById(BigInteger id);

    Mono<PageResultT<List<CategoryVO>>> findAllByPage( RequestPage<CategoryVO> requestPage);
}
