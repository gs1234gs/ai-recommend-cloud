package com.guanshiyun.rpc.chatrecommend;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.items.Item;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import reactor.core.publisher.Mono;


import java.util.List;

public interface AiChatClientRecommendServiceApi {
    //根据大模型获取推荐数据
//    public Mono<ResultT<List<Item>>> hostData(List<Item>  itemList);
    //向量化商品
     Mono<ResultT<List<String>>> embeddingProduct(List<ProductForEmbeddingApVO> product);
     //推荐商品
     Mono<ResultT<List<Long>>> recommendProduct(RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> recentProducts);
     //删除商品向量
     Mono<ResultT<Void>> embeddingDeleteProduct(Long productId);
     //根据关键字检索
     Mono<ResultT<List<Long>>> searchByKeyword(String keyWard, Integer topK);
     //gorse
     Mono<ResultT<List<String>>> gorse(String userId, int n);
     //gorse
     Mono<ResultT<List<String>>> gorse(String userId);
     //gorseSave
     Mono<ResultT<RowAffected>> gorse(Item item);
     //deleteGorse
     Mono<ResultT<RowAffected>> deleteGorse(String itemId);
}
