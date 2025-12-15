package com.guanshiyun.rpc.chatrecommend;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface AiChatClientRecommendServiceApi {
    //根据大模型获取推荐数据
//    public Mono<ResultT<List<Item>>> hostData(List<Item>  itemList);
    //向量化商品
    public Mono<ResultT<BigInteger>> embeddingProduct(List<ProductForEmbeddingApVO> product);
}
