package com.guanshiyun;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void reReadingTest2() {
    }

}
