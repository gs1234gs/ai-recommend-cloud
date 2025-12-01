package com.guanshiyun.service.chat;

import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface ChatRecordService {
    Mono<PageResultT<List<ChatRecordVO>>> findPageChat(RequestPage<ChatRecordVO> requestPage);
    Mono<BigInteger> save(ChatRecord chatRecord);
    Flux<ChatRecord> findCursorPageChat(RequestCursorPage<ChatRecord> requestCursorPage);
}
