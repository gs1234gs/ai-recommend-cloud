package com.guanshiyun.controller.sku;

import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.sku.vo.SKUFindVO;
import com.guanshiyun.controller.sku.vo.SKUGroupByProductIdVO;
import com.guanshiyun.controller.sku.vo.SKUSaveVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sku.SKUService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sku/")
@RestController
public class SKUController {
    private final SKUService skuService;
    //添加SKU
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody SKUSaveVO skuVO) {
        return skuService.save(skuVO)
                .map(id->{
                    log.info("添加成功，id为{}",id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("添加成功")
                            .data(id)
                            .build();
                })
                .onErrorResume(e->{
                    log.error("添加sku失败", e);
                    return Mono.just(
                            ResultT.<BigInteger>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("添加失败")
                                    .build());
                });
    }
    //删除SKU
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return skuService.deleteById(id)
                .then(Mono.fromCallable(() -> {
                    log.info("删除成功，id为{}",id);
                    return ResultT.<Void>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除成功")
                            .build();
                }))
                .onErrorResume(throwable ->{
                    log.error("删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除失败")
                                    .build());
                });
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<SKUVO>> findById(@PathVariable BigInteger id) {
        return skuService.findById(id)
                .map(sku ->
                        ResultT.<SKUVO>builder()
                                .code(HttpCodeConst.OK)
                                .msg("查询成功")
                                .data(sku)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<SKUVO>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }

    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<SKUGroupByProductIdVO>>>> findAllByPage(@RequestBody RequestPage<SKUFindVO> requestPage) {
        return skuService.findAllByPage(requestPage)
                .map(pageResultT ->{
                    log.info("查询成功");
                    return ResultT.<PageResultT<List<SKUGroupByProductIdVO>>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(pageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<SKUGroupByProductIdVO>>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
    /**
     * 根据商品id获取SKU列表
     * */
    @GetMapping("findByProductId/{productId}")
    public Mono<ResultT<List<SKUVO>>> findByProductId(@PathVariable BigInteger productId) {
        return skuService.findByProductId(productId)
                .collectList()
                .map(skus ->
                        ResultT.<List<SKUVO>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("查询成功")
                                .data(skus)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<List<SKUVO>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
    //批量删除
    @DeleteMapping("deleteAllById")
    public Mono<ResultT<Void>> deleteAllById(@RequestBody List<BigInteger> ids) {
        return skuService.deleteAllById(ids)
                .then(Mono.fromCallable(() -> {
                    log.info("批量删除成功，ids为{}",ids);
                    return ResultT.<Void>builder()
                            .code(HttpCodeConst.OK)
                            .msg("批量删除成功")
                            .build();
                }))
                .onErrorResume(throwable ->{
                            log.error("批量删除失败", throwable);
                            return Mono.just(
                                    ResultT.<Void>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("批量删除失败")
                                            .build()
                            );
                        }
                );
    }
    //根据id减库存
    @PutMapping("reduceStockById")
    public Mono<ResultT<Boolean>> reduceStockById(@RequestParam BigInteger id, @RequestParam Integer count) {
        return skuService.reduceStockById(id,count)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("修改库存失败", throwable);
                    return Mono.just(
                            ResultT.<Boolean>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("修改库存失败")
                                    .build()
                    );
                });
    }
    //加 存 库
    @PutMapping("addStockById")
    public Mono<ResultT<Boolean>> addStockById(@RequestParam BigInteger id, @RequestParam Integer count) {
        return skuService.addStockById(id,count)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("修改库存失败", throwable);
                    return Mono.just(
                            ResultT.<Boolean>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("修改库存失败")
                                    .build()
                    );
                });
    }

    //根据ids获取SKU
    @GetMapping("findBySkuIds")
    public Mono<ResultT<List<SKUVO>>> findBySkuIds(@RequestParam List<BigInteger> skuIds) {
       return skuService.findAllByIds(skuIds)
               .map(ResultT::success)
               .onErrorResume(e->Mono.just(ResultT.error()));
    }
}
