package com.guanshiyun.rpc.goodsapi.tag;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.profile.TagApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface TagApiService {
    Mono<ResultT<TagApiVO>> findById(BigInteger id);
    Mono<ResultT<List<TagApiVO>>> findByProductId(BigInteger productId);
    Mono<ResultT<List<TagApiVO>>> findByProductId(List<BigInteger> productId);
}
