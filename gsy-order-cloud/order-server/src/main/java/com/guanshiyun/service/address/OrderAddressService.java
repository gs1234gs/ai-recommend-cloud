package com.guanshiyun.service.address;

import com.guanshiyun.controller.address.vo.OrderAddressSaveVO;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;


import java.util.Collection;
import java.util.List;

public interface OrderAddressService {
    Mono<Long> save(OrderAddressSaveVO orderAddressSaveVO);
    //删除地址
    Mono<Void> deleteById(Long id);

    Mono<OrderAddressVO> findByOrderId(Object orderId);
    //获取批量
    Mono<List<OrderAddressVO>> findByUserId(Long userId);
    //根据用户id获取地址
    Mono<List<OrderAddressVO>> findByUserId();
    //根据订单id查询地址
    Mono<List<OrderAddressVO>> findByOrderIds(Collection<Long> orderIds);

    Mono<OrderAddressVO> findById(Long id);

    Mono<PageResultT<List<OrderAddressVO>>> findByPage(RequestPage<OrderAddressVO> requestPage);
}
