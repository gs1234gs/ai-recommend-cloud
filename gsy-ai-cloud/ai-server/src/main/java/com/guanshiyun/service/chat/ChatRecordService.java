package com.guanshiyun.service.chat;

import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;


public interface ChatRecordService {
    //分页查询对话
    Mono<PageResultT<List<ChatRecord>>> findPageChat(RequestPage<ChatRecord> requestPage);

    //修改保存对话
    Mono<BigInteger> save(ChatRecord chatRecord);

    Flux<ChatRecord> findCursorPageChat(RequestCursorPage<ChatRecord> requestCursorPage);
}
