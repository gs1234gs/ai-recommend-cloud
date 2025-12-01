package com.guanshiyun.controller.order;

import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.order.vo.PurChaseOrderSaveVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderVO;
import com.guanshiyun.jacksonBigNumberConfig.UseBigNumberSerialization;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.order.PurChaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/purChaseOrder/")
@RequiredArgsConstructor
@UseBigNumberSerialization
public class PurChaseOrderController {
    private final PurChaseOrderService purChaseOrderService;

    /**
     * 添加订单
     */
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody PurChaseOrderSaveVO purChaseOrderSaveVO) {
        return purChaseOrderService.save(purChaseOrderSaveVO)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("添加订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "添加订单失败"));
                });
    }

    //修改订单
    @PutMapping("updateById")
    public Mono<ResultT<BigInteger>> update(@RequestBody PurChaseOrderSaveVO purChaseOrderSaveVO) {
        return purChaseOrderService.updateById(purChaseOrderSaveVO)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("修改订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "修改订单失败"));
                });
    }

    //根据用户id查询订单
    @GetMapping("findByUserId")
    public Mono<ResultT<List<PurChaseOrderVO>>> findByUserId(
            @RequestParam BigInteger userId,
            @RequestParam(required = false, defaultValue = "10") Integer rows) {
        return purChaseOrderService.findByUserId(userId, rows)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "查询订单失败"));
                });
    }
    @PostMapping("findByUserIdPage")
    public Mono<ResultT<PageResultT<List<PurChaseOrderVO>>>> findByUserIdPage(
            @RequestBody RequestPage<PurChaseOrderVO> requestPage) {
        return purChaseOrderService.findByUserIdPage(requestPage)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "查询订单失败"));
                });
    }

    //根据多个·用户id批量返回订单
    @PostMapping("findByUserIds")
    public Mono<ResultT<List<PurChaseOrderVO>>> findByUserIds(
            @RequestBody List<BigInteger> userIds,
            @RequestParam(required = false, defaultValue = "10") Integer rows) {
        return purChaseOrderService.findByUserIds(userIds, rows)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "查询订单失败"));
                });
    }
    //根据订单id查询订单
    @GetMapping("findById/{id}")
    public Mono<ResultT<PurChaseOrderVO>> findById(@PathVariable BigInteger id) {
        return purChaseOrderService.findById(id)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询订单失败", throwable);
                    return Mono.just(ResultT.error(HttpCodeConst.INTERNAL_SERVER_ERROR, "查询订单失败"));
                });
    }
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<PurChaseOrderVO>>>> findByPage(@RequestBody RequestPage<PurChaseOrderVO> requestPage ) {
        return purChaseOrderService.findByPage(requestPage)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("查询订单失败", throwable);
                    return Mono.just(ResultT.error(
                            HttpCodeConst.INTERNAL_SERVER_ERROR,
                            "查询订单失败")
                    );
                });
    }
}
