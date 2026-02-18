package com.guanshiyun.rpc.goodsapi.product;

import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.ProductApiVO;
import com.guanshiyun.rpc.profile.ProductCustomerApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;


public interface ProductApiService {

    Mono<ResultT<CursorPageResult<List<ProductApiVO>>>> findCursor(RequestCursorPage<ProductApiVO> request);
    //通过id
    Mono<ResultT<ProductApiVO>> findProductById(BigInteger id);

    Mono<ResultT<List<ProductCustomerApiVO>>> findProductsByIds(List<BigInteger> ids);
}
