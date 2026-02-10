package com.guanshiyun.controller.address;

import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.address.vo.OrderAddressSaveVO;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
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
    private final MyBigInteger myBigInteger;
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

    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return orderAddressService.deleteById(id)
                .then(Mono.just(
                        ResultT.<Void>builder()
                                .code(HttpCodeConst.OK)
                                .msg("删除成功")
                                .build()
                ))
                .onErrorResume(throwable -> {
                    log.error("删除地址失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除地址失败")
                                    .build()
                    );
                });
    }

    //根据订单id获取 地址
    @GetMapping("/findByOrderId/{orderId}")
    public Mono<ResultT<OrderAddressVO>> findByOrderId(@PathVariable Object orderId) {
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
    @GetMapping("/findByUserId/{userId}")
    public Mono<ResultT<List<OrderAddressVO>>> findByUserId(@PathVariable Object userId) {
        return orderAddressService.findByUserId(myBigInteger.bigInteger(userId))
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
                    return Mono.just(
                            ResultT.<OrderAddressVO>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询地址失败")
                                    .build()
                    );
                });
    }
    @PostMapping("/findPage")
    public Mono<ResultT<PageResultT<List<OrderAddressVO>>>> findByPage(@RequestBody RequestPage<OrderAddressVO> requestPage) {
        return orderAddressService.findByPage(requestPage)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询地址失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<OrderAddressVO>>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询地址失败")
                                    .build()
                    );
                });
    }

}
