package com.guanshiyun.rpc.chatrecommend;

import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface AiChatClientRecommendServiceApi {
    //根据大模型获取热数据
    public Mono<ResultT<List<BigInteger>>> hostData();
}
