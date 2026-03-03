package com.guanshiyun.service.product.impl;

public class ProductKey {
    // Key 前缀
    public static final String REC_LIKE_KEY_PREFIX = "rec:like:";
    // 每次预加载的数量 (候选池大小)
    public static final int PRE_LOAD_SIZE = 100;
    // 每次接口返回的数量 (分页大小)
    public static final int PAGE_SIZE = 10;
    public static final String RECOMMEND_POOL_KEY_PREFIX = "recommend:pool:";
    public static final int RECOMMEND_POOL_SIZE = 50;
    private static final int CANDIDATE_POOL_SIZE = 100; // 每次从 Gorse 拉取多少条存入 Redis
    private static final long CACHE_EXPIRE_SECONDS = 3600; // 缓存过期时间 1 小时
}
