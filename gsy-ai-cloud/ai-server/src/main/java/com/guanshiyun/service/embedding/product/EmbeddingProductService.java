package com.guanshiyun.service.embedding.product;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface EmbeddingProductService {

     //批量保存
     Flux<Object> saveBatch(List<ProductForEmbeddingApVO> tList);
     //删除
     Mono<Void> deleteById(List<BigInteger> idList);
     // 推荐
     Mono<List<BigInteger>> recommendForUser(List<ProductForEmbeddingApVO> recentProducts, int topK);

}
