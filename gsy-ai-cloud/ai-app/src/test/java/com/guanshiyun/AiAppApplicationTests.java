package com.guanshiyun;

import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.repository.chat.ChatRecordRepository;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.service.chat.ChatRecordService;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class AiAppApplicationTests {

    @Autowired
    private OllamaChatModel ollamaModel;

    @Test
    void contextLoads(

    ) {

//        System.out.println(ollamaModel.call("你好，你是谁?"));
        ollamaModel.stream("今天是哪年哪月哪日，昨天昆明天气怎么样")
                .doOnNext(System.out::print)   // 每一段响应流输出到控制台
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> System.out.println("\n流式结束"))
                .blockLast();

    }

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ChatMemory chatMemory;

    @Test
    void reReadingTest() {
        ChatClient chatClient = chatClientBuilder.defaultAdvisors(
                PromptChatMemoryAdvisor.builder(chatMemory).build()
        ).build();
        String content = chatClient.prompt()
                .user("我叫徐庶")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");
        content = chatClient.prompt()
                .user("我叫什么?")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");
        content = chatClient.prompt()
                .user("我帅不帅?")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");
        content = chatClient.prompt()
                .user("我人品如何")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");
        content = chatClient.prompt()
                .user("我好不好?")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");
        content = chatClient.prompt()
                .user("我的名字叫什么")
                .call()
                .content();
        System.out.println("======================================================");
        System.out.println(content);
        System.out.println("======================================================");


    }

    @Autowired
    private ChatService chatService;

    @Test
    void reReadingTest2() {
        String name = EntityTableNameUtils.getName(ChatRecordContent.class);
        System.out.println( name);
        String name1 = EntityTableNameUtils.getName(ChatRecord.class);
        System.out.println( name1);

        System.out.println("======================================================");
        System.out.println("======================================================");
       chatService.chatAll(
                ReqChat.builder()
                        .content("我叫二牛,请问你是谁")
                        .build()

        ).contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                        .subscribe(System.out::println) ;
        System.out.println("======================================================");
    }
    @Autowired
    ChatRecordRepository chatRecordRepository;
    @Test
    void reReadingTest3() {

        String name = EntityTableNameUtils.getName(ChatRecordContent.class);
        System.out.println( name);
        String name1 = EntityTableNameUtils.getName(ChatRecord.class);
        System.out.println( name1);

        System.out.println("======================================================");
        chatService.chatAll(
                        ReqChat.builder()
                                .content("我是谁")
                                .conversationId(BigInteger.valueOf(176485086349230080L))
                                .build()
                ).contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .doOnNext(System.out::println)
                .onErrorResume(i->{
                    System.out.println("错误");
                    return Mono.just("我叫二牛");
                })
                .blockLast();
        System.out.println("======================================================");
    }

    @Autowired
    private ChatClient chatClient;

    @Test
    void reReadingTest4() {
        System.out.println("======================================================");
        chatClient.prompt()
                .user("我叫二牛,请问你是谁")
                .stream()
                .content()
                .doOnNext(System.out::println)
                .blockLast();
    }

//    @Bean
//    @Tool
//    public WebClient webClient() {
//        HttpClient httpClient = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 连接超时
//                .responseTimeout(Duration.ofMinutes(50)) // 响应超时（关键！）
//                .doOnConnected(conn ->
//                        conn.addHandlerLast(new ReadTimeoutHandler(300)) // 读取超时 300s
//                                .addHandlerLast(new WriteTimeoutHandler(30))
//                );
//
//        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
//
//        return WebClient.builder()
//                .clientConnector(connector)
//                .baseUrl("http://localhost:11434")
//                .build();
//    }
    @Autowired
    ChatRecordService chatRecordService;
    @Test
    void Test5() {
        chatRecordService.findPageChat(
                RequestPage.<ChatRecordVO>builder()
                        .pageNum(BigInteger.valueOf(1))
                        .pageSize(10)
                        .condition(ChatRecordVO.builder()
                                .title("二牛")
                                .build())
                        .build()
        )
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .subscribe(i->{
                 i.getRows().forEach(i1->{
                     System.out.println(i1);
                 });
                        }
                );
    }
    @Test
    void Test6() {
        chatRecordService.save(
                ChatRecord.builder()
                        .id(BigInteger.valueOf(143200670353195008L))
                        .title("二牛")
                        .creator(BigInteger.valueOf(1))
                        .createTime(LocalDateTime.now())
                        .build()
        ).contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .subscribe(System.out::println);
    }
    @Test
    void Test7() {
        StringBuffer stringBuffer = new StringBuffer();
        chatRecordService.findCursorPageChat(
                RequestCursorPage.<ChatRecord>builder()
//                        .lastId(BigInteger.valueOf(141934032957997056L))
                        .pageSize(10)
                        .condition(ChatRecord.builder()
                                .title("二牛")
                                .build())
//                        .order("ASC")
                        .build()
        )
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .doAfterTerminate(()->{
                    log.info("结束");
                    log.info(stringBuffer.toString());
                })
                .flatMap(i->{
                    log.info("开始");
                    stringBuffer.append(i);
                    stringBuffer.append("\n");
                    log.info("结束");
                    return Mono.just(i);
                })
                .collectList()
                .subscribe(i-> System.out.println(i));
    }
    @Test
    void testFindCursorPageChat_ShouldReturnRecords() {
        StepVerifier.create(
                        chatRecordService.findCursorPageChat(
                                        RequestCursorPage.<ChatRecord>builder()
                                                .lastId(BigInteger.valueOf(143200670353195008L)) //  用 long
                                                .pageSize(10)
                                                .condition(ChatRecord.builder()
                                                        .title("二牛")
                                                        .build())
                                                .build()
                                )
                                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                                .collectList() // 收集成 List
                )
                .assertNext(list -> {
                    System.out.println(" 查询成功，返回 " + list.size() + " 条数据");
                    list.forEach(chat ->
                            System.out.println("📄 id=" + chat.getId() + ", title=" + chat.getTitle())
                    );
                })
                .expectNextCount(1) // 根据实际情况调整期望数量
                .verifyComplete(); // 验证流正常结束
    }

    @Autowired
    private EmbeddingModel embeddingModel;
    // 内存向量库：item_id -> embedding
    private final Map<String, float[]> itemEmbeddingMap = new HashMap<>();
    // 商品元数据缓存
    private final Map<String, Map<String, String>> itemMetaMap = new HashMap<>();
    @Test
    void testHybridRecommendation() {
        // 模拟用户
        initializeItems();
        String userId = "user_123";
        boolean isNewUser = false; // ← 改为 false 测试老用户
        String lastClickedItemId = isNewUser ? null : "1001";

        List<EmbeddingSimilarItem> finalRecs;

        if (isNewUser) {
            //  新用户：直接让 Gorse 返回热门商品
            log.info("\n 检测到新用户，请求 Gorse 热门推荐...");
            Map<String, Object> gorseRequest = Map.of(
                    "user_id", userId,
                    "n", 3
            );
            List<Object> gorseResponse = gorse(gorseRequest); // 假设返回 List<ItemId>

            // 假设 Gorse 返回的是 ["1004", "1001", "1002"]
            finalRecs = gorseResponse.stream()
                    .map(id -> new EmbeddingSimilarItem(id.toString(), 0.0f))
                    .toList();
        } else {
            // 👤 老用户：先用 Qwen 召回，再让 Gorse 重排
            log.info("\n👤 老用户，使用 Qwen 语义召回 + Gorse 重排...");

            // 1. Qwen 召回 Top 50（比最终要的多，留给 Gorse 筛选）
            List<EmbeddingSimilarItem> candidates = recommendSimilar(lastClickedItemId, 50);
            List<String> candidateIds = candidates.stream()
                    .map(c -> c.itemId)
                    .toList();

            // 2. 发送给 Gorse 做 offline 推荐（重排）
            Map<String, Object> gorseRequest = Map.of(
                    "user_id", userId,
                    "candidates", candidateIds,
                    "n", 3
            );
            List<Object> gorseResponse = gorse(gorseRequest); // 返回重排后的 ID 列表

            // 3. 按 Gorse 返回顺序重建结果（相似度可保留或置0）
            Map<String, EmbeddingSimilarItem> candidateMap = candidates.stream()
                    .collect(Collectors.toMap(c -> c.itemId, c -> c));

            finalRecs = gorseResponse.stream()
                    .map(id -> {
                        String idStr = id.toString();
                        EmbeddingSimilarItem orig = candidateMap.get(idStr);
                        if (orig != null) {
                            return orig; // 保留原始相似度
                        } else {
                            return new EmbeddingSimilarItem(idStr, 0.0f); // 安全兜底
                        }
                    })
                    .toList();
        }

        // 🖨️ 统一输出
        log.info("\n 最终推荐结果（用户 {}）：", userId);
        for (EmbeddingSimilarItem rec : finalRecs) {
            String name = itemMetaMap.get(rec.itemId).get("name");
            if (rec.similarity > 0) {
                log.info("  → [ID: {}] {} (语义相似度: {})",
                        rec.itemId, name, String.format("%.4f", rec.similarity));
            } else {
                log.info("  → [ID: {}] {} (热门推荐)", rec.itemId, name);
            }
        }
    }

    // 计算余弦相似度（假设向量已归一化，可用点积）
    private float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("向量维度不一致");
        double dot = IntStream.range(0, a.length).mapToDouble(i -> a[i] * b[i]).sum();
        return (float) dot; // 归一化后，cosine = dot product
    }

    // 简易相似推荐（排除自身）
    private List<EmbeddingSimilarItem> recommendSimilar(String targetId, int topK) {

        float[] targetVec = itemEmbeddingMap.get(targetId);
        if (targetVec == null) {
            throw new IllegalArgumentException("商品ID不存在: " + targetId);
        }

        return itemEmbeddingMap.entrySet().stream()
                .filter(e -> !e.getKey().equals(targetId)) // 排除自己
                .map(e -> new EmbeddingSimilarItem(e.getKey(), cosineSimilarity(targetVec, e.getValue())))
                .sorted((a, b) -> Float.compare(b.similarity, a.similarity)) // 降序
                .limit(topK)
                .toList();
    }

    // 内部类：表示相似商品
    private static class EmbeddingSimilarItem {
        String itemId;
        float similarity;

        EmbeddingSimilarItem(String itemId, float similarity) {
            this.itemId = itemId;
            this.similarity = similarity;
        }
    }
    //模拟请求方法
    // 模拟 Gorse 行为（仅用于测试）
    List<Object> gorse(Map<String, Object> request) {
        if (request.containsKey("candidates")) {
            // 模拟重排：按原顺序返回前3个（实际 Gorse 会打乱）
            @SuppressWarnings("unchecked")
            List<String> candidates = (List<String>) request.get("candidates");
            return candidates.stream().limit((Integer) request.get("n")).collect(Collectors.toList());
        } else {
            // 模拟热门商品
            return Arrays.asList("1004", "1001", "1002"); // Kindle, iPhone, 扫地机
        }
    }
    private void initializeItems() {
        if (!itemMetaMap.isEmpty()) return; // 避免重复初始化

        List<Map<String, String>> items = Arrays.asList(
                Map.of("id", "1001", "name", "iPhone 15 Pro", "category", "手机", "brand", "Apple", "price", "8999"),
                Map.of("id", "1002", "name", "小米扫地机器人", "category", "家用电器", "brand", "小米", "price", "1999"),
                Map.of("id", "1003", "name", "耐克男子跑步鞋", "category", "运动鞋", "brand", "Nike", "price", "699"),
                Map.of("id", "1004", "name", "Kindle Paperwhite", "category", "电子书阅读器", "brand", "Amazon", "price", "1099"),
                Map.of("id", "1005", "name", "蒙牛纯牛奶 250ml*24盒", "category", "乳制品", "brand", "蒙牛", "price", "69")
        );

        for (Map<String, String> item : items) {
            String id = item.get("id");
            String pseudoText = String.format(
                    "商品名称：%s，品牌：%s，类目：%s，价格：%s元",
                    item.get("name"), item.get("brand"), item.get("category"), item.get("price")
            );
            float[] embedding = embeddingModel.embed(pseudoText);
            itemEmbeddingMap.put(id, embedding);
            itemMetaMap.put(id, item);
            log.info("商品ID: {} | 向量维度: {}", id, embedding.length);
        }
    }


}
