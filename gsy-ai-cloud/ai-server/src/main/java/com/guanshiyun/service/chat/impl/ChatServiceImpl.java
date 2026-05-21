package com.guanshiyun.service.chat.impl;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.cahetutil.SmartTitleExtractor;
import com.guanshiyun.chathistory.FormatChatHistory;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.content.ContentText;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.repositorymongodb.chat.ChatRecordContentMongodbRepository;
import com.guanshiyun.req.AllReqChat;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.util.JsonUtils;
import com.mongodb.client.result.UpdateResult;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.ContextView;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

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
    private final SnowflakePermanent snowflakePermanent;
    private final MyLong myLong;
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
        //获取会话id
        Long conversationId = reqChat.getConversationId();
        //获取聊天内容
        String content = reqChat.getContent();
        LocalDateTime now = LocalDateTime.now();
        //如果是第一次对话，创建会话
        if (Objects.isNull(conversationId)) {
            //创建会话标题
            String title = SmartTitleExtractor.extractFromSingle(content);
            //生成会话唯一id
            Long chatId = snowflakePermanent.nextId();
            /**
             * 插入会话记录
             * */
            return Flux.deferContextual(ctx -> {
                Long userId = myLong.findUserId(ctx);
                // 保存会话记录my sql
                return chatClient.prompt()
                        .user(content)
                        .stream()
                        .content()
                        .collectList()
                        .flatMapMany(contents -> {
                            String fullAiResponse = String.join("", contents);
                            ChatRecordContent chatRecordContent = ChatRecordContent.builder()
                                    .id(chatId)
                                    .title(title)
                                    .contentTexts(
                                            List.of(
                                                    ContentText.builder()
                                                            .id(snowflakePermanent.nextId())
                                                            .receiverContent(content)
                                                            .senderContent(fullAiResponse)
                                                            .build()
                                            )
                                    )
                                    .senderId(userId)
                                    .receiverId(ConstNumber.LONG_ONE)
                                    .build();
                            chatRecordContent.setCreator(userId)
                                    .setDelFlag((short) 0)
                                    .setCreateTime(now)
                                    .setUpdateTime(now);
                            //保存会话记录
                            return chatRecordContentMongodbRepository.save(chatRecordContent)
                                    .thenReturn(contents)
                                    .onErrorResume(saveEx -> {
                                        log.error("保存会话记录失败", saveEx);
                                        return Mono.just(contents);
                                    })
                                    .thenMany(Flux.fromIterable(contents));
                        });

            });
        }
        //如果不是第一次对话，就从数据库中获取会话记录
        // 已存在会话，追加内容
        return Flux.deferContextual(ctx -> {
                    Long userId = myLong.findUserId(ctx);
                    return chatClient.prompt()
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
                                        .set(BasePojo.Fields.updateTime, now)
                                        .set(BasePojo.Fields.updater, userId);
                                return reactiveMongoTemplate.updateFirst(
                                                query, update, ChatRecordContent.class
                                        )
                                        .thenMany(Flux.fromIterable(aiResponseSegments));
                            });

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
    public Mono<Tuple2<Flux<String>, Long>> chatFlux(ReqChat reqChat) {
        String content = reqChat.getContent();
        Long conversationId = reqChat.getConversationId();

        return Mono.deferContextual(ctx -> {
            // 1. 用户身份校验
            boolean isLoggedIn = myLong.hasKey(ctx);
            Long userId = isLoggedIn ? myLong.findUserId(ctx) : null;

            // 策略：未登录允许对话，但不持久化（或仅内存处理，这里沿用原逻辑：不保存记录，直接返回流）
            if (!isLoggedIn) {
                if (conversationId != null) {
                    // 未登录却传了ID，视为无效，按新对话处理（不保存）
                    return createNewUnsavedStream(content);
                }
                return createNewUnsavedStream(content);
            }

            // 2. 分支处理：首次对话 vs 继续对话
            if (Objects.isNull(conversationId)) {
                return handleFirstConversation(userId, content);
            } else {
                return handleExistingConversation(userId, conversationId, content);
            }
        });
    }

    /**
     * 处理未登录用户的临时对话（不保存）
     */
    private Mono<Tuple2<Flux<String>, Long>> createNewUnsavedStream(String content) {
        Long tempId = snowflakePermanent.nextId();
        Flux<String> stream = chatClient.prompt()
                .system(SHORT_ANSWER_SYSTEM_PROMPT)
                .user(content)
                .stream()
                .content();
        return Mono.just(Tuples.of(stream, tempId));
    }

    /**
     * 处理首次对话 (创建新文档)
     */
    private Mono<Tuple2<Flux<String>, Long>> handleFirstConversation(Long userId, String content) {
        Long chatId = snowflakePermanent.nextId();
        String title = SmartTitleExtractor.extractFromSingle(content);
        LocalDateTime now = LocalDateTime.now();

        // 准备最终要保存的完整对象结构（部分字段在流结束后填充）
        // 注意：这里我们选择“流结束后一次性保存”或者“先保存元数据，流结束后追加”。
        // 为了保持与原代码一致的“流结束后保存”逻辑，我们在 doOnComplete 中执行插入。
        // 但如果需要先有标题供前端列表刷新，可以先 insert 一个基础文档。
        // 这里采用：流结束后，Insert 完整的第一条记录。

        StringBuffer sb = new StringBuffer();

        Flux<String> stream = chatClient.prompt()
                .system(SHORT_ANSWER_SYSTEM_PROMPT)
                .user(content)
                .stream()
                .content()
                .doOnNext(sb::append)
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    String aiResponse = sb.toString();

                    // 构建完整的聊天记录文档
                    ChatRecordContent record = ChatRecordContent.builder()
                            .id(chatId)
                            .title(title) // 提取的标题
                            .senderId(userId)
                            .receiverId(ConstNumber.LONG_ONE) // 假设系统ID为1
                            .contentTexts(List.of(
                                    ContentText.builder()
                                            .id(snowflakePermanent.nextId())
                                            .receiverContent(content) // 用户问
                                            .senderContent(aiResponse) // AI 答
                                            .build()
                            ))
                            .build();
                    record.setCreator(userId)
                            .setCreateTime(now)
                            .setUpdater(userId)
                            .setUpdateTime(now)
                            .setDelFlag((short) 0);

                    // 异步保存整条记录
                    chatRecordContentMongodbRepository.save(record)
                            .subscribe(
                                    success -> log.debug("Saved new chat session: {}", chatId),
                                    error -> log.error("Failed to save new chat session", error)
                            );
                    sb.delete(0, sb.length());
                });

        return Mono.just(Tuples.of(stream, chatId));
    }

    /**
     * 处理已有会话 (查询 -> 构建上下文 -> 流式回答 -> 追加记录)
     */
    private Mono<Tuple2<Flux<String>, Long>> handleExistingConversation(Long userId, Long conversationId, String content) {

        // 1. 从 MongoDB 查询会话记录
        return chatRecordContentMongodbRepository.findById(conversationId)
                .switchIfEmpty(
                        // 重试机制：防止刚创建完查询不到的极端情况
                        Mono.delay(Duration.ofSeconds(1))
                                .then(chatRecordContentMongodbRepository.findById(conversationId))
                )
                .flatMap(chatRecord -> {
                    // 校验权限 (可选): 确保 creator == userId
                    if (!chatRecord.getCreator().equals(userId)) {
                        return Mono.error(new SecurityException("无权访问该会话"));
                    }

                    // 2. 构建历史上下文
                    List<ContentText> historyList = chatRecord.getContentTexts();
                    String chatHistory = FormatChatHistory.formatChatHistory(historyList);
                    // 格式化 Prompt (根据你的 FormatChatHistory 工具类逻辑)
                    String formattedPrompt = FormatChatHistory.CHAT_HISTORY.formatted(chatHistory, content);

                    // 3. 更新元数据 (更新时间) - 可以异步火射式更新，不阻塞流返回
                    chatRecord.setUpdater(userId);
                    chatRecord.setUpdateTime(LocalDateTime.now());

                    // 异步更新 updateTime，不等待完成即返回流
                    Query metaQuery = Query.query(Criteria.where(ChatRecordContent.Fields.id).is(conversationId));
                    Update metaUpdate = new Update()
                            .set(BasePojo.Fields.updateTime, chatRecord.getUpdateTime())
                            .set(BasePojo.Fields.updater, chatRecord.getUpdater());

                    reactiveMongoTemplate.updateFirst(metaQuery, metaUpdate, ChatRecordContent.class)
                            .subscribe(
                                    res -> log.debug("Updated chat metadata time"),
                                    err -> log.warn("Failed to update metadata time", err)
                            );

                    // 4. 调用 LLM 流式生成
                    StringBuffer sb = new StringBuffer();
                    Flux<String> stream = chatClient.prompt()
                            .system(SHORT_ANSWER_SYSTEM_PROMPT)
                            .user(formattedPrompt)
                            .stream()
                            .content()
                            .doOnNext(sb::append)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnComplete(() -> {
                                String aiResponse = sb.toString();

                                // 5. 追加新的对话内容到 MongoDB
                                ContentText newText = ContentText.builder()
                                        .id(snowflakePermanent.nextId())
                                        .receiverContent(content)
                                        .senderContent(aiResponse)
                                        .build();

                                Query query = Query.query(Criteria.where(ChatRecordContent.Fields.id).is(conversationId));
                                Update update = new Update()
                                        .push(ChatRecordContent.Fields.contentTexts, newText) // 追加到列表
                                        .set(BasePojo.Fields.updateTime, LocalDateTime.now())
                                        .set(BasePojo.Fields.updater, userId);

                                reactiveMongoTemplate.updateFirst(query, update, ChatRecordContent.class)
                                        .subscribe(
                                                success -> log.debug("Appended chat history for: {}", conversationId),
                                                error -> log.error("Failed to append chat history", error)
                                        );
                                sb.delete(0, sb.length());
                            });

                    return Mono.just(Tuples.of(stream, conversationId));
                })
                .switchIfEmpty(
                        // 如果重试后仍找不到，视为会话不存在，可以选择报错或转为新会话
                        // 这里选择转为新会话（类似原逻辑的 fallback，但原逻辑是递归调用，这里直接返回错误更清晰，或者按需创建新会话）
                        Mono.error(new IllegalArgumentException("Session not found: " + conversationId))
                );
    }

    /**
     * 删除会话记录
     *
     * @param id 会话 ID
     * @return Mono<Long> 删除影响的行数
     */
    @Override
    public Mono<Long> deleteChatById(Object id) {
        Long chatId = myLong.myLong(id);

        // 构建查询条件：根据 ID 查找且当前未删除
        Query query = Query.query(
                Criteria.where(ChatRecordContent.Fields.id).is(chatId)
                        .and(BasePojo.Fields.delFlag).is((short) 0)
        );

        // 构建更新操作：将 delFlag 设置为 1 (已删除)，并更新更新时间
        Update update = new Update()
                .set(BasePojo.Fields.delFlag, (short) 1)
                .set(BasePojo.Fields.updateTime, LocalDateTime.now());

        // 执行更新操作
        return reactiveMongoTemplate.updateFirst(query, update, ChatRecordContent.class)
                .map(UpdateResult::getModifiedCount) // 返回受影响的行数 (0 或 1)
                .onErrorResume(throwable -> {
                    log.error("Failed to logically delete chat record: {}", chatId, throwable);
                    return Mono.just(ConstNumber.LONG_ZERO);
                });
    }


    /**
     * 流式会话推荐
     * */
    @Override
    public Mono<Tuple2<Flux<String>, Long>> chatFluxRecommend(ReqChat reqChat) {
        return Mono.deferContextual(ctx -> {
            Long conversationId = reqChat.getConversationId();
            String userInput = reqChat.getContent();
            Long chatId = conversationId != null ? conversationId : snowflakePermanent.nextId();
            StringBuilder collectedText = new StringBuilder();
            return buildPrompt( userInput)
                    .map(prompt -> {
                        Flux<String> stream = chatClient
                                .prompt(prompt)
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
                                                            if (json != null) {
                                                                // 【关键修改】构建带标记的完整 payload
                                                                // 格式：<!--PRODUCT_START-->{...}<!--PRODUCT_END-->
                                                                String fullPayload = JsonUtils.PRODUCT_STREAM_START
                                                                        + json
                                                                        + JsonUtils.PRODUCT_STREAM_END;

                                                                // 发送完整片段，确保原子性
                                                                sink.next(fullPayload);

                                                                // 同时记录到 collectedText 用于持久化
                                                                // 在历史消息中保留标记，方便以后回放时也能解析出商品卡片
                                                                collectedText.append("\n").append(fullPayload).append("\n");
                                                            }
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
                                        {
                                            log.info("Chat record saved for: {}", signal);
                                            saveChatRecord(chatId, userInput, collectedText.toString(), ctx).subscribe();
                                        }
                                );


                        return Tuples.of(stream, chatId);
                    });
        });
    }


    private Mono<String> buildPrompt(String userInput) {
        // 直接使用基础模板 + 用户当前输入
        // 即使传入了 conversationId，我们也选择忽略它，保证每次推荐都是“ fresh start”
        String prompt = JsonUtils.PROMPT_TEMPLATE + "\n\n用户最新消息: " + userInput;

        return Mono.just(prompt);
    }
    private Mono<ChatRecordContent> findById(Long id) {
        return reactiveMongoTemplate.findById(id, ChatRecordContent.class);
    }

    private Mono<Void> saveChatRecord(Long chatId,
                                      String userContent,
                                      String aiContent,
                                      ContextView ctx) {

        if (!myLong.hasKey(ctx)) {
            return Mono.empty();
        }

        Long userId = myLong.findUserId(ctx);

        ContentText contentText = ContentText.builder()
                .id(snowflakePermanent.nextId())
                .receiverContent(userContent)
                .senderContent(aiContent)
                .build();

        LocalDateTime now = LocalDateTime.now();
        return reactiveMongoTemplate.updateFirst(
                Query.query(Criteria.where(ChatRecordContent.Fields.id).is(chatId)),
                new Update()
                        .push(ChatRecordContent.Fields.contentTexts, contentText)
                        .set(BasePojo.Fields.updateTime, now)
                        .set(BasePojo.Fields.updater, userId),
                        ChatRecordContent.class
        ).flatMap(r->{
            if (r.getMatchedCount() == 0) {
                // 如果没匹配到，说明会话文档不存在，尝试插入一条新文档
                ChatRecordContent newRecord = new ChatRecordContent();
                newRecord.setId(chatId);
                newRecord.setContentTexts(List.of(contentText));
                newRecord.setCreateTime(now);
                newRecord.setCreator(userId);
                return reactiveMongoTemplate.insert(newRecord);
            }
            return Mono.just(r);
        }).then();
    }


    @Override
    public Mono<AllReqChat> recommend(ReqChat reqChat) {
        String content = reqChat.getContent();
        Long chatId = snowflakePermanent.nextId();
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
            List<Long> productIds = extractProductIds(finalContent);
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
    private List<Long> extractProductIds(String response) {
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
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Flux<String> tFlux(ReqChat reqChat) {
        return chatClient.prompt()
//                .tools(productToolService)
                .user(reqChat.getContent())
                .stream()
                .content();
    }
}
