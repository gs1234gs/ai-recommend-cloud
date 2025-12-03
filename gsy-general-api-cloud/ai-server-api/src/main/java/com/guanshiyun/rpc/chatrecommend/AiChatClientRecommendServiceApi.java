package com.guanshiyun.rpc.chatrecommend;

import com.guanshiyun.items.Item;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AiChatClientRecommendServiceApi {
    //根据大模型获取推荐数据
    public Mono<ResultT<List<Item>>> hostData(List<P>);
}
