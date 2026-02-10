package com.guanshiyun.service.product;

import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface ProductService {
    Mono<BigInteger> save(ProductSaveVO productSaveVO);

    Mono<Long> deleteById(BigInteger id);

    Mono<PageResultT<List<ProductVO>>> findPage(RequestPage<ProductVO> requestPage);

    Mono<Long> save(List<ProductSaveVO> productSaveVOList);

    Mono<Void> deleteAllById(List<BigInteger> ids);

    Mono<ProductVO> findById(BigInteger id);

    Mono<CursorPageResult<List<ProductVO>>> findCursorListProductVO(RequestCursorPage<ProductVO> requestCursorPage);

    Mono<BigInteger> saveProduct(ProductSaveVO productSaveVO);
}
