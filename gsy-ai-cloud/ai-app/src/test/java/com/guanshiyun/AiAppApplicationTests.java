package com.guanshiyun;

import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.chat.ChatRecord;
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
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.math.BigInteger;
import java.time.LocalDateTime;
@Slf4j
@SpringBootTest
class AiAppApplicationTests {

    @Autowired
    private OllamaChatModel ollamaModel;

    @Test
    void contextLoads(

    ) {

//        System.out.println(ollamaModel.call("你好，你是谁?"));
        ollamaModel.stream("你好，你是谁?")
                .doOnNext(System.out::print)   // 每一段响应流输出到控制台
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> System.out.println("\n流式结束"))
                .blockLast();

    }

    @Test
    void chatClientTest(@Autowired OllamaChatModel ollamaModel) {
        ChatClient chatClient = ChatClient.builder(ollamaModel)
                .defaultSystem("请以你是{name}的身份来回答，性别：{sex},年龄：{age}，").build();
        String content = chatClient.prompt()
                .user("关羽帅不帅?")
                .advisors(new SimpleLoggerAdvisor(), new ReReading())
                .system(p -> p.param("name", "小千")
                        .param("sex", "女")
                        .param("age", "18"))
                .call()
                .content();
        System.out.println(content);

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
        chatService.chatFlux(
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
                RequestPage.<ChatRecord>builder()
                        .pageNum(BigInteger.valueOf(1))
                        .pageSize(10)
                        .condition(ChatRecord.builder()
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
                                                .lastId(BigInteger.valueOf(143200670353195008L)) // ✅ 用 long
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
                    System.out.println("✅ 查询成功，返回 " + list.size() + " 条数据");
                    list.forEach(chat ->
                            System.out.println("📄 id=" + chat.getId() + ", title=" + chat.getTitle())
                    );
                })
                .expectNextCount(1) // 根据实际情况调整期望数量
                .verifyComplete(); // 验证流正常结束
    }
}
