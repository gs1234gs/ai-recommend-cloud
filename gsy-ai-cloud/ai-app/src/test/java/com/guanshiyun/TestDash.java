package com.guanshiyun;

import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.service.embedding.EmbeddingProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;


@SpringBootTest
public class TestDash {

    @Autowired
    public ChatService chatService;
    @Autowired
    EmbeddingProductService embeddingProductService;

    @Test
    public void test() {

        ReqChat req = ReqChat.builder()
                .conversationId(Long.valueOf(43939561600L))
                .content("机器人")
                .flag(false)
                .build();

        chatService.tFlux(req)
                .map(r -> {
                    System.out.println("Streaming responses: " + r); // 这里应该会逐行打印
                    return r;
                })
                .blockLast(); // <--- 关键：阻塞直到流结束！

        // 只有当所有数据打印完毕后，代码才会运行到这里
        System.out.println("=== 流式传输结束 ===");
    }

    @Test
    public void test43() {
        ReqChat req = ReqChat.builder()
                .conversationId(Long.valueOf(43939561600L))
                .content("机器人")
                .flag(false)
                .build();

        try {
            // 1. 调用接口获取 Mono
            chatService.chatFluxRecommend(req)
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(tuple -> {
                        // 2. 拿到 Flux<String> (假设 tuple 的第一个元素是 Flux)
                        Flux<String> flux = tuple.getT1();

                        // 3. 处理流并阻塞等待完成
                        // 使用 blockLast() 会阻塞当前线程直到 Flux 完成或出错
                        flux.map(r -> {
                                    System.out.println("Streaming responses: " + r);
                                    return r;
                                })
                                .blockLast(); // <--- 关键：阻塞直到流结束！

                        return Mono.just(tuple.getT2()); // 如果有需要返回的第二个值
                    })
                    .block(); // 阻塞外层的 Mono 完成

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 只有当所有数据打印完毕后，代码才会运行到这里
        System.out.println("=== 流式传输结束 ===");
    }

    public static void main(String[] args) throws IOException {
        WebClient webClient = WebClient.create("http://localhost:8087");
        ResponseEntity<Void> block = webClient.get()
                .uri("/recommend/67")
                .retrieve()
                .toBodilessEntity()
                .block();
        Assertions.assertNotNull(block);
        System.out.println(block.getBody());


    }

    @Test
    public void test44() {
        List<Long> product = embeddingProductService.searchKeyword("机器人", 3);
        System.out.println("======================");
        System.out.println(product);
    }
}