package com.guanshiyun.service.review.impl;

import com.db.cursorQuery.ReactiveQuery;
import com.guanshiyun.repository.review.ReviewProductRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.review.ReviewProduct;
import com.guanshiyun.service.review.ReviewProductService;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewProductServiceImpl implements ReviewProductService {
    private final ReviewProductRepository reviewProductRepository;
    private final ReactiveQuery reactiveQuery ;
    @Override
    public Mono<Object> save(Object object) {
        ReviewProduct reviewProduct = BeanConvertUtil.toBean(object, ReviewProduct.class);
        return reviewProductRepository.save(reviewProduct).cast(Object.class);
    }

    @Override
    public Mono<PageResultT<List<ReviewProduct>>> list(Long pageNum, int pageSize, String content) {
        return reactiveQuery.createQuery(ReviewProduct.class,
                        RequestPage.<ReviewProduct>builder().pageNum(pageNum).pageSize(pageSize)
                .build())
                .like("content", content)
                .page();
    }
}
