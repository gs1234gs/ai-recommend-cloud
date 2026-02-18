package com.guanshiyun.service.chat.impl;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.cahetutil.SmartTitleExtractor;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.chathistory.FormatChatHistory;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.content.ContentText;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.repository.chat.ChatRecordRepository;
import com.guanshiyun.repositorymongodb.chat.ChatRecordContentMongodbRepository;
import com.guanshiyun.req.AllReqChat;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.service.chat.impl.utils.JsonUtils;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.ContextView;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ChatServiceImpl
 * <p>
 * 类功能：
 * - 提供聊天接口服务，支持一次性返回和流式返回聊天结果
 * - 自动创建会话（第一次聊天）并保存聊天记录到 MySQL 与 MongoDB
 * - 对已存在会话，追加聊天内容并更新元信息
 * - 集成 AI 聊天客户端（ChatClient）生成聊天回复
 */
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


    private static final String SHORT_ANSWER_SYSTEM_PROMPT = """
            你是一个高效的智能助手。
            回答要求：
            1. 内容简洁，直给结论
            2. 不要长篇解释
            3. 优先使用要点或短句
            4. 除非用户明确要求，否则不展开说明
            """;

    /**
     * 一次性返回完整聊天记录
     *
     * @param reqChat 聊天请求对象，包含 conversationId 和用户输入内容
     * @return Flux<String> 返回 AI 回复文本流
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
                        // 调用 AI 客户端生成聊天回复，并保存到 MongoDB
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
        // 已存在会话，追加内容
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
     * 流式返回聊天内容
     *
     * @param reqChat 聊天请求对象
     * @return Flux<String> AI 回复内容流
     */
    @Override
    public Mono<Tuple2<Flux<String>, BigInteger>> chatFlux(ReqChat reqChat) {
        //如果id为null,就为第一次对话，检索关键词作为标题
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
            return Mono.deferContextual(ctx -> {
                //没有登陆，允许对话，但是不保存记录
                if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                    // 未登录：不保存，但返回流 + newConvId
                    Flux<String> stream = chatClient.prompt()
                            .system(SHORT_ANSWER_SYSTEM_PROMPT)
                            .user(content)
                            .stream()
                            .content();
                    return Mono.just(Tuples.of(stream, chatId));
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
                        .then(Mono.defer(() ->
                                {
                                    StringBuffer sb = new StringBuffer();
                                    //  调用 LLM 流式生成
                                    Flux<String> stream = chatClient.prompt()
                                            .system(SHORT_ANSWER_SYSTEM_PROMPT)
                                            .user(content)
                                            .stream()
                                            .content()
                                            .doOnNext(sb::append)
                                            .publishOn(Schedulers.boundedElastic())
                                            //是时返回数据，最后一次保存
                                            .doOnComplete(() -> {

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
                                                        .subscribe(
                                                                success -> log.debug("Saved chat history"),
                                                                error -> log.error("Failed to save chat history", error)
                                                        );
                                            });
                                    return Mono.just(Tuples.of(stream, chatId));
                                }
                        ));

            });
        }
        //非首次对话：恢复上下文
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                // 未登录但传了 conversationId？视为无效，按新对话处理（可选策略）
                return chatFlux(ReqChat.builder().content(content).build());
            }

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
                    .flatMap(chat -> {
                        //mongodb获取历史记录，检索上下文
                        return chatRecordContentMongodbRepository.findById(chat.getId())
                                .switchIfEmpty(
                                        Mono.delay(Duration.ofSeconds(ConstNumber.INT_ONE)) // 异步等待1秒，不阻塞线程
                                                .then(chatRecordContentMongodbRepository.findById(conversationId)) // 重试查询
                                                .switchIfEmpty(Mono.empty())
                                )
                                .flatMap(chatRecordContent -> {
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
                                            .then(Mono.defer(() ->
                                                            //加入历史对话
                                                    {
                                                        StringBuffer sb = new StringBuffer();
                                                        // 流式调用 LLM
                                                        Flux<String> stream = chatClient.prompt()
                                                                .system(SHORT_ANSWER_SYSTEM_PROMPT)
                                                                .user(formattedChatHistory)
                                                                .stream()
                                                                .content()
                                                                .doOnNext(sb::append)
                                                                .publishOn(Schedulers.boundedElastic())
                                                                //是时返回数据，最后一次保存
                                                                .doOnComplete(() -> {
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
                                                                    ).subscribe(
                                                                            success -> log.debug("Update success"),
                                                                            error -> log.error("Failed to update chat history", error)
                                                                    );
                                                                });
                                                        return Mono.just(Tuples.of(stream, conversationId));
                                                    }
                                            ));

                                });
                    });
        });
    }

    /**
     * 删除会话记录
     *
     * @param id 会话 ID
     * @return Mono<Long> 删除影响的行数
     */
    @Override
    public Mono<Long> deleteChatById(Object id) {
        return databaseClient.sql("DELETE FROM chat_record WHERE id = :id")
                .bind(ChatRecord.Fields.id, myBigInteger.bigInteger(id))
                .fetch()
                .rowsUpdated()
                .onErrorResume(throwable -> Mono.just(ConstNumber.LONG_ZERO));
    }

    @Override
    public Mono<Tuple2<Flux<String>, BigInteger>> chatFluxRecommend(ReqChat reqChat) {
        return Mono.deferContextual(ctx -> {
            BigInteger conversationId = reqChat.getConversationId();
            String userInput = reqChat.getContent();
            BigInteger chatId = conversationId != null ? conversationId : snowflakePermanent.nextId();
            StringBuilder collectedText = new StringBuilder();

            return buildPrompt(conversationId, userInput)
                    .map(prompt -> {

                        Flux<String> stream = chatClient.prompt(prompt)
                                .stream()
                                .chatResponse()
                                .<String>handle((chatResponse, sink) -> {
                                    Generation gen = chatResponse.getResult();

                                    AssistantMessage msg = gen.getOutput();

                                    // 文本流
                                    String text = msg.getText();
                                    if (text != null && !text.isBlank()) {
                                        collectedText.append(text);
                                        sink.next(text); // String
                                    }

                                    // 工具商品 JSON
                                    if (msg.hasToolCalls()) {
                                        for (AssistantMessage.ToolCall toolCall : msg.getToolCalls()) {
                                            if ("searchProduct".equals(toolCall.name())) {
                                                try {
                                                    Map<String, Object> args = JsonUtils.parseMap(toolCall.arguments());
                                                    Object productsObj = args.get("toolProductList");
                                                    if (productsObj instanceof List<?> products) {
                                                        for (Object product : products) {
                                                            Map<String, Object> wrapper = Map.of("product", product);
                                                            String json = JsonUtils.toJson(wrapper);
                                                            sink.next(Objects.requireNonNull(json)); // String
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    log.warn("Parse tool product failed", e);
                                                }
                                            }
                                        }
                                    }
                                })
                                .publishOn(Schedulers.boundedElastic())
                                .doFinally(signal ->
                                        saveChatRecord(chatId, userInput, collectedText.toString(), ctx).subscribe());



                        return Tuples.of(stream, chatId);
                    });
        });
    }


    private Mono<String> buildPrompt(BigInteger conversationId, String userInput) {
        String baseTemplate = JsonUtils.PROMPT_TEMPLATE;
        if (Objects.isNull(conversationId)) {
            return Mono.just(baseTemplate + "\n\n用户最新消息: " + userInput);
        }

        return chatRecordContentMongodbRepository.findById(conversationId)
                .map(history -> {
                    String chatHistory = FormatChatHistory.formatChatHistory(history.getContentTexts());
                    // 历史内容 + 模板 + 用户最新输入
                    return FormatChatHistory.CHAT_HISTORY.formatted(chatHistory, chatHistory)
                            + "\n" + baseTemplate
                            + "\n\n用户最新消息: " + userInput;
                })
                .switchIfEmpty(Mono.just(baseTemplate + "\n\n用户最新消息: " + userInput));
    }
    private Mono<Void> saveChatRecord(BigInteger chatId,
                                      String userContent,
                                      String aiContent,
                                      ContextView ctx) {

        if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
            return Mono.empty();
        }

        BigInteger userId = myBigInteger.bigInteger(
                ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
        );

        ContentText contentText = ContentText.builder()
                .id(snowflakePermanent.nextId())
                .receiverContent(userContent)
                .senderContent(aiContent)
                .build();

        return reactiveMongoTemplate.updateFirst(
                Query.query(Criteria.where(ChatRecordContent.Fields.id).is(chatId)),
                new Update()
                        .push(ChatRecordContent.Fields.contentTexts, contentText)
                        .set(BasePojo.Fields.updateTime, LocalDateTime.now())
                        .set(BasePojo.Fields.updater, userId),
                ChatRecordContent.class
        ).then();
    }


    @Override
    public Mono<AllReqChat> recommend(ReqChat reqChat) {
        String content = reqChat.getContent();
        BigInteger chatId = snowflakePermanent.nextId();
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("""
                        你是一个专业的电商购物助手。
                        
                        当用户询问商品时，请按以下规则回答：
                        1. 先用 1~3 句自然语言推荐商品，说明理由（如品牌、功能、适用场景等）；
                        2. 然后在最后一行单独输出一个 JSON 对象，格式严格为：
                           {"recommend_products": [101, 102, 103]}
                        
                         重要：
                        - 必须包含自然语言部分！禁止只输出 JSON！
                        - JSON 必须在最后一行，前面只能有换行；
                        - 不要使用反引号、代码块、注释或任何额外文字；
                        - 商品 ID 必须是数字，放在数组中。
                        """),
                new UserMessage(content)
        ));

        return Mono.fromCallable(() -> {
            String fullResponse = chatClient.prompt(prompt).call().content();
            if (fullResponse == null) {
                fullResponse = "";
            }

            // 关键兜底：如果只有 JSON，补自然语言前缀
            String finalContent = fullResponse.trim();
            if (finalContent.startsWith("{") && finalContent.endsWith("}") && finalContent.contains("\"recommend_products\"")) {
                finalContent = "根据您的需求，我们为您推荐以下商品：\n" + fullResponse;
            }

            // 提取商品 ID
            List<BigInteger> productIds = extractProductIds(finalContent);
            String naturalLanguageOnly = removeJsonLine(finalContent);
            return AllReqChat.builder()
                    .conversationId(chatId)
                    .content(naturalLanguageOnly)      // 用户看到的内容
                    .productIdList(productIds)  // 用于加载商品卡片
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String removeJsonLine(String response) {
        if (response == null || response.isEmpty()) {
            return "";
        }

        // 按行分割（兼容 \n 和 \r\n）
        String[] lines = response.split("\\r?\\n");
        if (lines.length == 0) {
            return response;
        }

        // 检查最后一行是否是 {"recommend_products": [...]}
        String lastLine = lines[lines.length - 1].trim();
        if (lastLine.startsWith("{") && lastLine.endsWith("}") && lastLine.contains("\"recommend_products\"")) {
            // 移除最后一行
            if (lines.length == 1) {
                // 只有一行且是 JSON → 返回兜底文案（不应发生，因已有兜底）
                return "根据您的需求，我们为您推荐以下商品。";
            } else {
                // 保留前面所有行
                return String.join("\n", Arrays.copyOf(lines, lines.length - 1)).trim();
            }
        }

        // 最后一行不是 JSON，返回原内容
        return response.trim();
    }

    //  提取方法：兼容 JSON 在任意位置（但优先匹配最后一个）
    private List<BigInteger> extractProductIds(String response) {
        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }

        // 匹配所有 {"recommend_products": [...]}，取最后一个（最可能是结尾的）
        Pattern pattern = Pattern.compile("\\{\"recommend_products\":\\s*\\[([^]]*?)]}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        String lastMatch = null;
        while (matcher.find()) {
            lastMatch = matcher.group(1);
        }

        if (lastMatch != null && !lastMatch.trim().isEmpty()) {
            return Arrays.stream(lastMatch.split(","))
                    .map(String::trim)
                    .filter(s -> s.matches("\\d+"))
                    .map(BigInteger::new)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
