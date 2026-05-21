package com.guanshiyun.service.aitool.impl;

import com.guanshiyun.service.aitool.PurchaseOrderToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderToolServiceImpl implements PurchaseOrderToolService {


    @Tool(
            name = "placeOrder",
            description = "当用户想要购买商品时，必须使用此工具。" +
                    "它将用户ID、商品ID和数量作为参数，返回订单ID。" +
                    "注意：此工具仅返回订单ID。如果订单创建失败，将返回 null或者错误提示内容。"
    )
    @Override
    public Object placeOrder(String userId, String productId, int quantity) {
        log.info("用户 {} 下单购买商品 {}，数量 {}", userId, productId, quantity);
        return null;
    }
}
