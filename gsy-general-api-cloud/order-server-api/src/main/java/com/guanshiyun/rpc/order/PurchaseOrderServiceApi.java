package com.guanshiyun.rpc.order;

import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.responsepojo.ResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface PurchaseOrderServiceApi {
    //根据用户od获取购买记录
    Mono<ResultT<List<PurchaseOrderVOApi>>> findByUserId(BigInteger userId);
}
