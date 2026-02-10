package com.guanshiyun.service.aitool.impl;

import com.guanshiyun.service.aitool.ProductToolService;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductToolServiceImpl implements ProductToolService {
    private final EmbeddingProductService embeddingProductService;
//    private final Scheduler boundedElastic = Schedulers.boundedElastic();

//    @Override
//    @Tool(
//            name = "searchProduct",
//            description = "根据关键词搜索商品（10字内），返回最多5个商品ID"
//    )
//    public List<BigInteger> searchProduct(String content) {
//        if (!StringUtils.hasText(content)) {
//            return List.of();
//        }
//        // 在 boundedElastic 线程中执行阻塞调用
//        return Mono.fromCallable(() ->
//                        embeddingProductService.searchByKeyword(content.trim(), 5)
//                                .doOnSuccess(result -> {
//                                    log.info(result.toString());
//                                })
//                                .block(Duration.ofSeconds(8))
//                )
//                .subscribeOn(boundedElastic)
//                .onErrorReturn(List.of())
//                .block(); // ← 这个 block 是在 boundedElastic 线程，不阻塞 Netty
//    }
    @Override
    @Tool(
            name = "searchProduct",
            description = "根据关键词搜索商品（10字内），返回最多5个商品ID"
    )
    public List<BigInteger> searchProduct(@ToolParam(description = "用户需求描述") String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }
        log.info("[TOOL ACTUALLY CALLED] Query: {}", content); // ← 关键
        List<BigInteger> productList = embeddingProductService.searchKeyword(content.trim(), 5);
        return productList;
    }
}
