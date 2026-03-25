package com.guanshiyun.rpc.goodsapi.product;

import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.ProductCustomerApiVO;
import reactor.core.publisher.Mono;


import java.util.List;


public interface ProductApiService {

    Mono<ResultT<CursorPageResult<List<ProductApiVO>>>> findCursor(RequestCursorPage<ProductApiVO> request);
    //通过id
    Mono<ResultT<ProductApiVO>> findProductById(Long id);

    Mono<ResultT<List<ProductCustomerApiVO>>> findProductsByIds(List<Long> ids);

    Mono<ResultT<List<ProductApiVO>>> findProductVOByIds(List<Long> ids);
}
