package com.guanshiyun;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.req.ReqChat;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.service.chat.ChatService;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RecommendTest {
    @Autowired
    private ChatService chatService;

    @Autowired
    private EmbeddingProductService embeddingProductService;

//    @Test
//    public void testRecommend() {
//
//
//        ReqChat build = ReqChat.builder()
//                .conversationId(null)
//                .content("推荐 Python编程从入门到实践 第三版")
//                .flag(false)
//                .build();
//        Tuple3<Flux<String>, List<BigInteger>, BigInteger> result = chatService.chatFluxRecommend(build).block();
//
//        if (result != null) {
//            Flux<String> stream = result.getT1();
//            List<BigInteger> productIds = result.getT2();
//            BigInteger chatId = result.getT3();
//
//            System.out.println("=== 商品ID列表 ===");
//            System.out.println(productIds);
//
//            System.out.println("=== 会话ID ===");
//            System.out.println(chatId);
//
//            System.out.println("=== AI 流式回复内容 ===");
//            // 订阅 Flux 才能触发流并收集内容
//            StringBuilder fullResponse = new StringBuilder();
//            stream.doOnNext(part -> {
//                System.out.print(part); // 实时打印（模拟流式效果）
//                fullResponse.append(part);
//            }).blockLast(); // 阻塞直到流结束
//
//            System.out.println("\n=== 完整回复 ===");
//            System.out.println(fullResponse.toString());
//        } else {
//            System.out.println("Result is null!");
//        }
//    }
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
        ReqChat req = ReqChat.builder()
                .conversationId(null)
                .content("推荐 Python编程从入门到实践 第三版")
                .flag(false)
                .build();

        StepVerifier.create(chatService.recommend(req))
                .consumeNextWith(chunk -> System.out.println("Chunk: " + chunk))
                .consumeNextWith(chunk -> System.out.println("Chunk: " + chunk))
                // ... 或者用 expectNextMatches 多次
                .expectComplete()
                .verify();
    }
    @Test
    public void testRecommend() throws InterruptedException {
        ReqChat req = ReqChat.builder()
                .conversationId(BigInteger.valueOf(43939561600L))
                .content("推荐 Python编程从入门到实践 第三版")
                .flag(false)
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        chatService.chatFluxRecommend(req)
                .doOnNext(tuple -> {
                    Flux<String> stream = tuple.getT1();
                    BigInteger chatId = tuple.getT2();

                    System.out.println("Conversation ID: " + chatId);
                    System.out.println("Streaming responses:");

                    // 订阅内部的 Flux<String> 才能真正触发流式输出！
                    stream
                            .doOnNext(chunk -> System.out.println( chunk))
                            .doOnError(err -> {
                                err.printStackTrace();
                                latch.countDown();
                            })
                            .doOnComplete(latch::countDown)
                            .subscribe(); // 👈 关键：订阅内部 Flux
                })
                .subscribe(); // 订阅外层 Mono

        // 等待流结束（最多 10 秒）
        boolean completed = latch.await(100, TimeUnit.SECONDS);
        if (!completed) {
            System.err.println("Timeout waiting for stream to complete!");
        }
    }


    @Autowired
    ProductApiService productService;

    @Test
    public void testEmbeddingProductService4() {
        StepVerifier.create(
                        productService.findProductsByIds(List.of(BigInteger.valueOf(23), BigInteger.valueOf(24)))
                                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(17L)))
                )
                .consumeNextWith(products -> {
                    System.out.println(products);
                    // 可加断言，如 assertNotNull(products), assertEquals(2, products.size()) 等
                })
                .verifyComplete();
    }
    }











