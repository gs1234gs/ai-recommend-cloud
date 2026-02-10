package com.guanshiyun.aienums;

public class AiApiUrl {
    public static final String EMBEDDING = "/embedding";
    // 向量化商品
    public static final String EMBEDDING_PRODUCT_SAVE_BATCH = EMBEDDING + "/product/saveBatch";
    // 删除商品向量
    public static final String EMBEDDING_PRODUCT_DELETE_BY_PRODUCT_ID = EMBEDDING + "/product/deleteByProductId/{productId}";
    // 推荐商品
    public static final String EMBEDDING_PRODUCT_RECOMMEND_FOR_USER = EMBEDDING + "/product/recommendForUser";
    //关键字检索
    public static final String EMBEDDING_PRODUCT_RECOMMEND_BY_KEY_WARD = EMBEDDING + "/product/searchKeyWard";
}
