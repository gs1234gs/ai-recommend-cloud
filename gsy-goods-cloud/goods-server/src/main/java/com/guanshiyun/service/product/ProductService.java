package com.guanshiyun.service.product;

import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;


import java.util.List;

public interface ProductService {
    Mono<Long> save(ProductSaveVO productSaveVO);

    Mono<Long> deleteById(Long id);

    Mono<PageResultT<List<ProductVO>>> findPage(RequestPage<ProductVO> requestPage);

    Mono<Long> save(List<ProductSaveVO> productSaveVOList);

    Mono<Void> deleteAllById(List<Long> ids);

    Mono<ProductVO> findById(Long id);

    Mono<CursorPageResult<List<ProductVO>>> findCursorListProductVO(RequestCursorPage<ProductVO> requestCursorPage);

    Mono<Long> saveProduct(ProductSaveVO productSaveVO);


}
