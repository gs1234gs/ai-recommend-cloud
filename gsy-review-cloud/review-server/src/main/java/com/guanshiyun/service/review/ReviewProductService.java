package com.guanshiyun.service.review;

import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.review.ReviewProduct;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewProductService {

    Mono<Object> save(Object object);

    Mono<PageResultT<List<ReviewProduct>>> list(Long pageNum, int pageSize,String content);
}
