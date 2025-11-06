package com.guanshiyun.controller.chat;

import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.chat.ChatRecordService;
import com.guanshiyun.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/")
public class ChatController {

    private final ChatService chatService;
    private final ChatRecordService chatRecordService;


    //一次性对话
    @PostMapping("chatAll")
    public Flux<ResultT<String>> chatAll(@RequestBody ReqChat reqChat){

        return chatService.chatAll(reqChat)
                .map(content-> ResultT.<String>builder()
                        .code(HttpCodeConst.OK)
                        .msg("对话成功")
                        .data( content)
                        .build())
                .onErrorResume(throwable ->
                    Mono.just(ResultT.<String>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("对话失败")
                            .build())
                );
    }
    //流式对话
    @PostMapping("fluxChat")
    public Flux<ResultT<String>> fluxChat(@RequestBody ReqChat reqChat){

        return chatService.chatFlux(reqChat)
                .map(content-> ResultT.<String>builder()
                        .code(HttpCodeConst.OK)
                        .msg("对话成功")
                        .data( content)
                        .build()
                )
                .onErrorResume(throwable ->{
                    return Mono.just(ResultT.<String>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("对话失败")
                            .build());
                });
    }

    //删除对话
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteChat(@PathVariable BigInteger id){

        return chatService.deleteChatById( id)
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
    public Mono<ResultT<PageResultT<List<ChatRecord>>>> findChat(@RequestBody RequestPage<ChatRecord> requestPage){

        return chatRecordService.findPageChat(requestPage)
                .map(pageResultT ->{
                    return ResultT.<PageResultT<List<ChatRecord>>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("获取成功")
                            .data(pageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("获取失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<ChatRecord>>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("获取失败")
                            .build());
                });
    }
    //修改对话标题
    @PutMapping("saveChat")
    public Mono<ResultT<BigInteger>> chatSave(@RequestBody ChatRecord chatRecord){
   return chatRecordService.save(chatRecord)
           .map(saveId ->{
               return ResultT.<BigInteger>builder()
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
    public Flux<ChatRecord> chatCursor(@RequestBody RequestCursorPage<ChatRecord> requestCursorPage){
        return chatRecordService.findCursorPageChat(requestCursorPage);
    }
}
