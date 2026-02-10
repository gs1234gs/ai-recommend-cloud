package com.guanshiyun;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.req.AllReqChat;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;
import reactor.util.function.Tuple3;

import java.math.BigInteger;
import java.util.List;

@SpringBootTest
public class RecommendTest {
    @Autowired
    private ChatService chatService;

    @Autowired
    private EmbeddingProductService embeddingProductService;

    @Test
    public void testRecommend() {


        ReqChat build = ReqChat.builder()
                .conversationId(null)
                .content("推荐 Python编程从入门到实践 第三版")
                .flag(false)
                .build();
        Tuple3<Flux<String>, List<BigInteger>, BigInteger> result = chatService.chatFluxRecommend(build).block();

        if (result != null) {
            Flux<String> stream = result.getT1();
            List<BigInteger> productIds = result.getT2();
            BigInteger chatId = result.getT3();

            System.out.println("=== 商品ID列表 ===");
            System.out.println(productIds);

            System.out.println("=== 会话ID ===");
            System.out.println(chatId);

            System.out.println("=== AI 流式回复内容 ===");
            // 订阅 Flux 才能触发流并收集内容
            StringBuilder fullResponse = new StringBuilder();
            stream.doOnNext(part -> {
                System.out.print(part); // 实时打印（模拟流式效果）
                fullResponse.append(part);
            }).blockLast(); // 阻塞直到流结束

            System.out.println("\n=== 完整回复 ===");
            System.out.println(fullResponse.toString());
        } else {
            System.out.println("Result is null!");
        }
    }
    @Test
    public void testEmbeddingProductService() {
        List<BigInteger> block = embeddingProductService.recommendForUser(
                List.of(
                        ProductForEmbeddingApVO.builder()
                                .title("Python编程从入门到实践 第三版 ")
                                .build()
                )
                , 3)
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(17L)))
                .block();
        System.out.println(block);
    }

    @Test
    public void testEmbeddingProductService2() {
        ReqChat build = ReqChat.builder()
                .conversationId(null)
                .content("推荐 Python编程从入门到实践 第三版")
                .flag(false)
                .build();

        // 阻塞获取 Tuple3
        AllReqChat block = chatService.recommend(build)
                .block();
        System.out.println(block);

    }
}
