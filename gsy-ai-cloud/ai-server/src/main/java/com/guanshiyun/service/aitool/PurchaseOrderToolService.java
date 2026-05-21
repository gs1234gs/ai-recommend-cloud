package com.guanshiyun.service.aitool;

public interface PurchaseOrderToolService {
    //下单
    Object placeOrder(String userId, String productId, int quantity);
}
