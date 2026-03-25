package com.guanshiyun;

import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;


@SpringBootTest
public class TestDash {

    @Autowired
    public ChatService chatService;

    @Test
    public void test() {

        ReqChat req = ReqChat.builder()
                .conversationId(Long.valueOf(43939561600L))
                .content("推荐 Python编程从入门到实践 第三版")
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

    public static void main(String[] args) throws IOException {
        WebClient webClient = WebClient.create("http://localhost:8087");
        ResponseEntity<Void> block = webClient.get()
                .uri("/recommend")
                .retrieve()
                .toBodilessEntity()
                .block();
        Assertions.assertNotNull(block);
        System.out.println(block.getBody());


    }
}