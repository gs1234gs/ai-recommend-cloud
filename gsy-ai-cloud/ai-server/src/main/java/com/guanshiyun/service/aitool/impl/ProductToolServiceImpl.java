package com.guanshiyun.service.aitool.impl;

import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.profile.ProductCustomerApiVO;
import com.guanshiyun.service.aitool.ProductToolService;
import com.guanshiyun.service.embedding.EmbeddingProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


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
    @Override
    @Tool(
            name = "searchProduct",
            description = "当用户想要查找某种商品（如'连衣裙'、'机械键盘'），但你不知道具体商品ID时，必须首先使用此工具。" +
                    "它根据关键词在数据库中检索，返回最匹配的最多5个商品ID。" +
                    "注意：此工具仅返回ID列表。如果找不到商品，将返回空列表。"
    )
    public List<Long> searchProduct(@ToolParam(description = "用户搜索商品的关键词，例如：'夏季新款连衣裙'") String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }
        log.info("触发商品id获取: {}", content); // ← 关键
        List<Long> productList = embeddingProductService.searchKeyword(content.trim(), 5);
        return productList;
    }

    @Override
    @Tool(
            name = "toolProductList",
            description = "当你已经拥有一组商品ID（例如通过 searchProduct 获取），需要获取这些商品的详细信息（名称、价格、图片、库存）以便展示给用户时，使用此工具。" +
                    "它将商品ID列表转换为完整的商品信息对象列表。" +
                    "如果传入的ID列表无效或商品不存在，将返回空列表。"
    )
    public List<ProductCustomerApiVO> toolProductList(@ToolParam(description = "需要查询详细信息的商品ID列表")
                                                          List<Long> productList) {

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
