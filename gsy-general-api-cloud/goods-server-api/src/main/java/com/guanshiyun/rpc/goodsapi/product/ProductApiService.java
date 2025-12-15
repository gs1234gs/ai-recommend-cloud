package com.guanshiyun.rpc.goodsapi.product;

import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.ProductApiVO;
import reactor.core.publisher.Mono;

import java.util.List;


public interface ProductApiService {

    Mono<ResultT<CursorPageResult<List<ProductApiVO>>>> findCursor(RequestCursorPage<ProductApiVO> request);
}
