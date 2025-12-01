package com.guanshiyun.rpc.goodsapi.tag;

import com.guanshiyun.rpc.profile.TagApiVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface TagApiService {
    Mono<TagApiVO> findById(BigInteger id);
    Mono<TagApiVO> findByProductId(BigInteger productId);
}
