package com.guanshiyun.rpc.goodsapi.recommend.impl;

import com.guanshiyun.rpc.config.GoodsWebClientRpc;
import com.guanshiyun.rpc.goodsapi.recommend.ProductRecommendApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRecommendApiServiceImpl implements ProductRecommendApiService {
    private final GoodsWebClientRpc goodsWebClientRpc;
}
