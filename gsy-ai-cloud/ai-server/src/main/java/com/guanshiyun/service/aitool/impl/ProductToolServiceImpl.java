package com.guanshiyun.service.aitool.impl;

import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.profile.ProductCustomerApiVO;
import com.guanshiyun.service.aitool.ProductToolService;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductToolServiceImpl implements ProductToolService {
    private final EmbeddingProductService embeddingProductService;
    private final ProductApiService productApiService;
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
        log.info("触发商品id获取: {}", content); // ← 关键
        List<BigInteger> productList = embeddingProductService.searchKeyword(content.trim(), 5);
        return productList;
    }

    @Override
    @Tool(
            name = "toolProductList",
            description = "根据商品ID列表获取商品列表"
    )
    public List<ProductCustomerApiVO> toolProductList(List<BigInteger> productList) {
        log.info("🔹 [TOOL SYNC] 开始同步获取商品列表: {}", productList);

        try {
            // 安全阻塞：设置超时，避免线程卡死
            return productApiService.findProductsByIds(productList)
                    .timeout(Duration.ofSeconds(10)) // ⏱ 超时保护
                    .map(res -> {
                        if (res == null) {
                            log.warn("【NULL RESULT】API 返回 null");
                            return new ArrayList<ProductCustomerApiVO>();
                        }
                        List<ProductCustomerApiVO> data = res.getData();
                        if (data == null) {
                            log.warn("【DATA NULL】res.getData() 为 null");
                            return new ArrayList<ProductCustomerApiVO>();
                        }
//                        log.info(" 获取到 {} 个商品: {}", data.size(), data);
                        return data;
                    })
                    .doOnError(e -> log.error(" API 调用失败", e))
                    .block(); //  同步阻塞，但有超时保护
        } catch (RuntimeException e) {
            // 捕获 TimeoutException / RuntimeException 等
            log.error("toolProductList 阻塞调用失败", e);
            return Collections.emptyList();
        }
    }
}
