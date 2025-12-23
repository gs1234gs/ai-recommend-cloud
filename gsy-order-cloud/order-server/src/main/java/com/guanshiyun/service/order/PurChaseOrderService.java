package com.guanshiyun.service.order;

import com.guanshiyun.controller.order.vo.PurChaseOrderSaveVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
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
    Mono<PurChaseOrderVO> findById(BigInteger id);

    Mono<PageResultT<List<PurChaseOrderVO>>> findByPage(RequestPage<PurChaseOrderVO> requestPage);

    Mono<PageResultT<List<PurChaseOrderVO>>> findByUserIdPage(RequestPage<PurChaseOrderVO> requestPage);

    //获取指定条数的用户订单
    Mono<List<PurChaseOrderVO>> findByRows(Integer rows);
}
