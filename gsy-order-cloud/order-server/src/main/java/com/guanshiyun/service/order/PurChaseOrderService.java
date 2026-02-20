package com.guanshiyun.service.order;

import com.guanshiyun.controller.order.vo.PurChaseOrderSaveVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderVO;
import com.guanshiyun.controller.order.vo.PurchaseOrderDetailVO;
import com.guanshiyun.controller.order.vo.PurchaseOrderSearchVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface PurChaseOrderService {
    //添加订单
    Mono<BigInteger> save(PurChaseOrderSaveVO purChaseOrderSaveVO);

    //修改订单
    Mono<BigInteger> updateById(PurChaseOrderSaveVO purChaseOrderSaveVO);

    //根据用户id查询
    Mono<List<PurChaseOrderVO>> findByUserId(BigInteger userId,Integer rows);

    //根据用户id查询
    Mono<List<PurChaseOrderVO>> findByUserIds(List<BigInteger> userIds, Integer rows);

    //根据id查询
    Mono<PurchaseOrderDetailVO> findById(BigInteger id);

    Mono<PageResultT<List<PurChaseOrderVO>>> findByPage(RequestPage<PurchaseOrderSearchVO> requestPage);

    Mono<PageResultT<List<PurChaseOrderVO>>> findByUserIdPage(RequestPage<PurchaseOrderSearchVO> requestPage);

    //获取指定条数的用户订单
    Mono<List<PurchaseOrderVOApi>> findByRows(Integer rows);

    Mono<Boolean> deleteById(BigInteger id);
}
