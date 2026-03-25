package com.guanshiyun.controller.chat;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.req.AllReqChat;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.service.chat.impl.ChatRecordServiceImpl;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/")
public class ChatController {

    private final ChatService chatService;
    private final ChatRecordServiceImpl chatRecordService;
     ObjectMapper mapper = new ObjectMapper();


    //一次性对话
    @PostMapping("chatAll")
    public Flux<ResultT<String>> chatAll(@RequestBody ReqChat reqChat){

        return chatService.chatAll(reqChat)
                .map(content-> ResultT.<String>builder()
                        .code(HttpCodeConst.OK)
                        .msg("对话成功")
                        .data( content)
                        .build()
                )
                .onErrorResume(throwable ->
                    Mono.just(ResultT.<String>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("对话失败")
                            .build())
                );
    }
    @PostMapping("fluxChat")
    public Flux<String> fluxChat(@RequestBody ReqChat reqChat, ServerHttpResponse response) {
        return chatService.chatFlux(reqChat)
                .doOnNext(tuple -> {
                    // 在流开始前设置响应头（只执行一次）
                    response.getHeaders().set("X-Conversation-ID", tuple.getT2().toString());
                    response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM); // 或 text/plain
                })
                .flatMapMany(Tuple2::getT1) // 展开 Flux<String>
                .map(token -> "data: " + token + "\n\n") // 转为标准 SSE 格式（可选）
                .onErrorResume(throwable -> {
                    log.error("对话失败", throwable);
                    // 返回一个错误事件，前端可监听
                    String errorEvent = "event: error\ndata: " +
                            JSON.toJSONString(ResultT.<String>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("对话失败: " + throwable.getMessage())
                                    .build()) + "\n\n";
                    return Mono.just(errorEvent);
                });
    }

    @PostMapping("recommendChat")
    public Mono<ResultT<AllReqChat>> recommendChat(@RequestBody ReqChat reqChat) {

        return chatService.recommend(reqChat)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("",throwable);
                    return Mono.just(ResultT.<AllReqChat>builder().code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("服务器错误")
                            .build());
                });
    }
    //流式
    @PostMapping(value = "/recommendFluxChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> recommendFluxChat(@RequestBody ReqChat reqChat, ServerWebExchange exchange) {
        return chatService.chatFluxRecommend(reqChat)
                .doOnNext(tuple -> {
                    Long chatId = tuple.getT2();
                    if (!exchange.getResponse().isCommitted()) {
                        exchange.getResponse().getHeaders().set("X-Conversation-ID", chatId.toString());
                    }
                })
                .flatMapMany(Tuple2::getT1)
                .onErrorResume(throwable -> {
                    log.error("Recommend chat stream error", throwable);
                    // 错误信息也直接返回纯文本，Spring 会自动包装
                    return Flux.just("{\"msg\":\"服务器内部错误\",\"code\":500}");
                });
    }


    //删除对话
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteChat(@PathVariable Object id){

        return chatService.deleteChatById(id)
                .map(deleteCount ->{
                    return ResultT.<Long>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除成功")
                            .data(deleteCount)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("删除失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("删除失败")
                            .build());
                });
    }
    //获取对话
    @PostMapping("findChat")
    public Mono<ResultT<PageResultT<List<ChatRecordVO>>>> findChat(@RequestBody RequestPage<ChatRecordVO> requestPage){

        return chatRecordService.findPageChat(requestPage)
                .map(pageResultT ->{
                    return ResultT.<PageResultT<List<ChatRecordVO>>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("获取成功")
                            .data(pageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("获取失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<ChatRecordVO>>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取失败")
                            .build());
                });
    }
    //修改对话标题
    @PutMapping("saveChat")
    public Mono<ResultT<Long>> chatSave(@RequestBody ChatRecordContent chatRecord){
   return chatRecordService.save(chatRecord)
           .map(saveId ->{
               return ResultT.<Long>builder()
                       .code(HttpCodeConst.OK)
                       .msg("保存成功")
                       .data(saveId)
                       .build();
           });
    }
    /**
     * 游标分页
     * */
    @PostMapping("findCursorChat")
    public Mono<ResultT<List<ChatRecordVO>>> chatCursor(@RequestBody RequestCursorPage<ChatRecord> requestCursorPage){
        return chatRecordService.findCursorPageChat(requestCursorPage)
                .mapNotNull(chatRecord -> BeanConvertUtil.toBean(chatRecord, ChatRecordVO.class))
                .collectList()
                .map(ResultT::success);
    }
}
