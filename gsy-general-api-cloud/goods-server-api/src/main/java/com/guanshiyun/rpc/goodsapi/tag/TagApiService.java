package com.guanshiyun.rpc.goodsapi.tag;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.profile.TagApiVO;
import reactor.core.publisher.Mono;


import java.util.List;

public interface TagApiService {
    Mono<ResultT<TagApiVO>> findById(Long id);
    Mono<ResultT<List<TagApiVO>>> findByProductId(Long productId);
    Mono<ResultT<List<TagApiVO>>> findByProductId(List<Long> productId);
}
