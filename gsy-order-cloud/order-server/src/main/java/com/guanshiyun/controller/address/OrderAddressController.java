package com.guanshiyun.controller.address;

import com.guanshiyun.controller.address.vo.OrderAddressSaveVO;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.address.OrderAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class OrderAddressController {
    private final OrderAddressService orderAddressService;
    //添加地址和保存地址
    @PostMapping("/save")
    public Mono<ResultT<BigInteger>> save(@RequestBody OrderAddressSaveVO orderAddressSaveVO) {
        return orderAddressService.save(orderAddressSaveVO)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("添加地址失败", throwable);
                    return Mono.error(new Exception("添加地址失败"));
                });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return orderAddressService.deleteById(id)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("删除地址失败", throwable);
                    return Mono.error(new Exception("删除地址失败"));
                });
    }
    //根据订单id获取 地址
    @GetMapping("/findByOrderId/{orderId}")
    public Mono<ResultT<OrderAddressVO>> findByOrderId(@PathVariable BigInteger orderId) {
        return orderAddressService.findByOrderId(orderId)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询地址失败", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });
    }
    //批量订单id获取地址
    @PostMapping("/findByOrderIds")
    public Mono<ResultT<List<OrderAddressVO>>> findByOrderIds(@RequestBody List<BigInteger> orderIds) {
        return orderAddressService.findByOrderIds(orderIds)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("批量查询地址失败", throwable);
                    return Mono.error(new Exception("批量查询地址失败"));
                });
    }
    //根据用户id获取地址
    @GetMapping("/findByUserId")
    public Mono<ResultT<List<OrderAddressVO>>> findByUserId(@RequestParam BigInteger userId) {
        return orderAddressService.findByUserId(userId)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询地址失败", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });
    }
    //根据id获取地址
    @GetMapping("/findById/{id}")
    public Mono<ResultT<OrderAddressVO>> findById(@PathVariable BigInteger id) {
        return orderAddressService.findById(id)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询地址失败", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });
    }
}
