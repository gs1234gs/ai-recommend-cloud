package com.guanshiyun.service.chat;

import com.guanshiyun.req.ReqChat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {
    //一次性返回对话
    Flux<String> chatAll(ReqChat reqChat);
    //分段返回对话
    Flux<String> chatFlux(ReqChat reqChat);

    //删除对话
    Mono<Long> deleteChatById(Object id);
}
