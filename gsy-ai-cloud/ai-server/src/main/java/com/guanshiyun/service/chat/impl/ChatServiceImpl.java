package com.guanshiyun.service.chat.impl;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.cahetutil.SmartTitleExtractor;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.chathistory.FormatChatHistory;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.content.ContentText;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.repository.chat.ChatRecordRepository;
import com.guanshiyun.repositorymongodb.chat.ChatRecordContentMongodbRepository;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatRecordContentMongodbRepository chatRecordContentMongodbRepository;
    private final ChatRecordRepository chatRecordRepository;
    private final SnowflakePermanent snowflakePermanent;
    private final DatabaseClient databaseClient;
    private final MyBigInteger myBigInteger;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    /**
     * 一次性返回聊天记录
     */
    @Override
    public Flux<String> chatAll(ReqChat reqChat) {
        //创建对象，保存记录
        ChatRecord chatRecord = ChatRecord.builder().build();
        //获取会话id
        BigInteger conversationId = reqChat.getConversationId();
        //获取聊天内容
        String content = reqChat.getContent();

        //如果是第一次对话，创建会话
        if (Objects.isNull(conversationId)) {
            //创建会话标题
            String title = SmartTitleExtractor.extractFromSingle(content);
            //生成会话唯一id
            BigInteger chatId = snowflakePermanent.nextId();
            chatRecord.setId(chatId);
            chatRecord.setTitle(title);
            //创建时间
            chatRecord.setCreateTime(LocalDateTime.now());
            /**
             * 插入会话记录
             * */
            return Flux.deferContextual(ctx -> {
                BigInteger userId = myBigInteger.bigInteger(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                );
                chatRecord.setCreator(userId);
                // 保存会话记录my sql
                return databaseClient.sql(
                                "insert into chat_record(id,title,creator,updater,create_time,update_time) values(:id,:title,:creator,:updater,:createTime,:updateTime)"
                        )
                        .bind(ChatRecord.Fields.id, chatRecord.getId())
                        .bind(ChatRecord.Fields.title, chatRecord.getTitle())
                        .bind(ChatRecord.Fields.creator, chatRecord.getCreator())
                        .bindNull(ChatRecord.Fields.updater, BigInteger.class)
                        .bind(ChatRecord.Fields.createTime, chatRecord.getCreateTime())
                        .bindNull(ChatRecord.Fields.updateTime, LocalDateTime.class)
//                        .bind(ChatRecord.Fields.delFlag, chatRecord.getDelFlag())
                        .fetch()
                        .rowsUpdated()
                        .thenMany(Flux.defer(() ->
                                chatClient.prompt()
                                        .user(content)
                                        .stream()
                                        .content()
                                        .collectList()
                                        .flatMapMany(contents -> {
                                            String fullAiResponse = String.join("", contents);
                                            ChatRecordContent chatRecordContent = ChatRecordContent.builder()
                                                    .id(chatId)
                                                    .creator(userId)
                                                    .contentTexts(
                                                            List.of(
                                                                    ContentText.builder()
                                                                            .id(snowflakePermanent.nextId())
                                                                            .receiverContent(content)
                                                                            .senderContent(fullAiResponse)
                                                                            .build()
                                                            )
                                                    )
                                                    .delFlag((short) 0)
                                                    .createTime(LocalDateTime.now())
                                                    .senderId(userId)
                                                    .receiverId(BigInteger.ONE)
                                                    .updateTime(LocalDateTime.now())
                                                    .build();
                                            //保存会话记录
                                            return chatRecordContentMongodbRepository.save(chatRecordContent)
                                                    .thenReturn(contents)
                                                    .onErrorResume(saveEx -> {
                                                        log.error("保存会话记录失败", saveEx);
                                                        return Mono.just(contents);
                                                    })
                                                    .thenMany(Flux.fromIterable(contents));
                                        })

                        ));

            });
        }
        //如果不是第一次对话，就从数据库中获取会话记录
        return Flux.deferContextual(ctx -> {
                    BigInteger userId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );
                    // 1. 更新 ChatRecord 元信息（如更新时间
                    chatRecord.setUpdater(userId);
                    chatRecord.setUpdateTime(LocalDateTime.now());
                    chatRecord.setId(conversationId);
                    return r2dbcUpdateHelper.updateIgnoreNull(
                                    EntityTableNameUtils.getName(ChatRecord.class),
                                    chatRecord,
                                    ChatRecord.Fields.id)
                            .thenMany(Flux.defer(() ->
                                    chatClient.prompt()
                                            .user(content)
                                            .stream()
                                            .content()
                                            //是时返回数据，最后一次保存
                                            .collectList()
                                            .flatMapMany(aiResponseSegments -> {

                                                String fullAiResponse = String.join("", aiResponseSegments);
                                                // 3. 构建要追加的 ContentText
                                                ContentText newContentText = ContentText.builder()
                                                        .id(snowflakePermanent.nextId())
                                                        .receiverContent(reqChat.getContent())
                                                        .senderContent(fullAiResponse)
                                                        .build();
                                                Query query = Query.query(Criteria.where(ChatRecordContent.Fields.id).is(conversationId));
                                                Update update = new Update()
                                                        .push(ChatRecordContent.Fields.contentTexts, newContentText)
                                                        .set(ChatRecordContent.Fields.updateTime, LocalDateTime.now())
                                                        .set(ChatRecordContent.Fields.updater, userId);
                                                return reactiveMongoTemplate.updateFirst(
                                                                query, update, ChatRecordContent.class
                                                        )
                                                        .thenMany(Flux.fromIterable(aiResponseSegments));
                                            })


                            ));

                })
                .onErrorResume(ex -> {
                    log.error("对话失败", ex);
                    return Flux.just("抱歉，对话过程中出现错误，请稍后再试。");
                });
    }

    /**
     * 流式返回
     */
    @Override
    public Flux<String> chatFlux(ReqChat reqChat) {
        //如果id为null,就为第一次对话，检索关键词作为标题
        //创建对象，保存记录
        ChatRecord chatRecord = ChatRecord.builder().build();
        //获取会话id
        BigInteger conversationId = reqChat.getConversationId();
        //获取聊天内容
        String content = reqChat.getContent();
        //如果是第一次对话，创建会话
        StringBuffer sb = new StringBuffer();
        if (Objects.isNull(conversationId)) {
            //创建会话标题
            String title = SmartTitleExtractor.extractFromSingle(content);
            //生成会话唯一id
            BigInteger chatId = snowflakePermanent.nextId();
            chatRecord.setId(chatId);
            chatRecord.setTitle(title);
            //创建时间
            chatRecord.setCreateTime(LocalDateTime.now());
            /**
             * 插入会话记录
             * */
            return Flux.deferContextual(ctx -> {
                //没有登陆，允许对话，但是不保存记录
                if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                    return chatClient.prompt()
                            .user(content)
                            .stream()
                            .content();
                }
                //登陆了，保存记录
                BigInteger userId = myBigInteger.bigInteger(
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                );
                chatRecord.setCreator(userId);
                // 保存会话记录my sql
                return databaseClient.sql(
                                "insert into chat_record(id,title,creator,updater,create_time,update_time) values(:id,:title,:creator,:updater,:createTime,:updateTime)"
                        )
                        .bind(ChatRecord.Fields.id, chatRecord.getId())
                        .bind(ChatRecord.Fields.title, chatRecord.getTitle())
                        .bind(ChatRecord.Fields.creator, chatRecord.getCreator())
                        .bindNull(ChatRecord.Fields.updater, BigInteger.class)
                        .bind(ChatRecord.Fields.createTime, chatRecord.getCreateTime())
                        .bindNull(ChatRecord.Fields.updateTime, LocalDateTime.class)
//                        .bind(ChatRecord.Fields.delFlag, chatRecord.getDelFlag())
                        .fetch()
                        .rowsUpdated()
                        .thenMany(Flux.defer(() ->
                                chatClient.prompt()
                                        .user(content)
                                        .stream()
                                        .content()
                                        .doOnNext(sb::append)
                                        .publishOn(Schedulers.boundedElastic())
                                        //是时返回数据，最后一次保存
                                        .doAfterTerminate(() -> {
                                            String newContent = sb.toString();
                                            ChatRecordContent chatRecordContent = ChatRecordContent.builder()
                                                    .id(chatId)
                                                    .creator(userId)
                                                    .contentTexts(
                                                            List.of(
                                                                    ContentText.builder()
                                                                            .id(snowflakePermanent.nextId())
                                                                            .receiverContent(content)
                                                                            .senderContent(newContent)
                                                                            .build()
                                                            )
                                                    )
                                                    .delFlag((short) 0)
                                                    .createTime(LocalDateTime.now())
                                                    .senderId(userId)
                                                    .receiverId(BigInteger.ONE)
                                                    .updateTime(LocalDateTime.now())
                                                    .build();
                                            sb.delete(ConstNumber.INT_ZERO, sb.length());
                                            chatRecordContentMongodbRepository.save(chatRecordContent)
                                                    .subscribe();
                                        })
                        ));

            });
        }
        //如果不是第一次对话，就从数据库中获取会话记录
        return Flux.deferContextual(ctx -> {
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            //先校验mysql是否存在该对话
            return chatRecordRepository.findById(conversationId)
                    .switchIfEmpty(
                            Mono.delay(Duration.ofSeconds(ConstNumber.INT_ONE)) // 异步等待1秒，不阻塞线程
                                    .then(chatRecordRepository.findById(conversationId)) // 重试查询
                                    .switchIfEmpty(Mono.empty())
                    )
                    .flatMapMany(chat -> {
                        //mongodb获取历史记录，检索上下文
                        return chatRecordContentMongodbRepository.findById(chat.getId())
                                .switchIfEmpty(
                                        Mono.delay(Duration.ofSeconds(ConstNumber.INT_ONE)) // 异步等待1秒，不阻塞线程
                                                .then(chatRecordContentMongodbRepository.findById(conversationId)) // 重试查询
                                                .switchIfEmpty(Mono.empty())
                                )
                                .flatMapMany(chatRecordContent -> {
                                    // 更新 ChatRecord 元信息
                                    List<ContentText> contentTexts = chatRecordContent.getContentTexts();
                                    String chatHistory = FormatChatHistory.formatChatHistory(contentTexts);
                                    String formattedChatHistory = FormatChatHistory.CHAT_HISTORY.formatted(chatHistory, chatHistory);
                                    chatRecord.setUpdater(userId);
                                    chatRecord.setUpdateTime(LocalDateTime.now());
                                    chatRecord.setId(conversationId);
                                    return r2dbcUpdateHelper.updateIgnoreNull(
                                                    EntityTableNameUtils.getName(ChatRecord.class),
                                                    chatRecord,
                                                    ChatRecord.Fields.id)
                                            .thenMany(Flux.defer(() ->
                                                    //加入历史对话
                                                    chatClient.prompt()
                                                            .user(formattedChatHistory)
                                                            .stream()
                                                            .content()
                                                            .doOnNext(sb::append)
                                                            .publishOn(Schedulers.boundedElastic())
                                                            //是时返回数据，最后一次保存
                                                            .doAfterTerminate(() -> {
                                                                ContentText newContentText = ContentText.builder()
                                                                        .id(snowflakePermanent.nextId())
                                                                        .receiverContent(reqChat.getContent())
                                                                        .senderContent(sb.toString())
                                                                        .build();
                                                                Query query = Query.query(
                                                                        Criteria
                                                                                .where(ChatRecordContent.Fields.id)
                                                                                .is(conversationId));
                                                                Update update = new Update()
                                                                        .push(ChatRecordContent.Fields.contentTexts, newContentText)
                                                                        .set(ChatRecordContent.Fields.updateTime, LocalDateTime.now())
                                                                        .set(ChatRecordContent.Fields.updater, userId);
                                                                sb.delete(ConstNumber.INT_ZERO, sb.length());
                                                                reactiveMongoTemplate.updateFirst(
                                                                        query, update, ChatRecordContent.class
                                                                ).subscribe();
                                                            })
                                            ));

                                });
                    });
        });
    }

    @Override
    public Mono<Long> deleteChatById(BigInteger id) {
        return databaseClient.sql("DELETE FROM chat_record WHERE id = :id")
                .bind(ChatRecord.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .onErrorResume(throwable -> Mono.just(ConstNumber.LONG_ZERO));
    }

}
