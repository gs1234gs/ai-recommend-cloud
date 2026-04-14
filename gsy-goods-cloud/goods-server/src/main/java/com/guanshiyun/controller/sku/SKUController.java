package com.guanshiyun.controller.sku;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sku/")
@RestController
public class SKUController {
    private final SKUService skuService;
    //添加SKU
    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody SKUSaveVO skuVO) {
        return skuService.save(skuVO)
                .map(id->{
                    log.info("添加成功，id为{}",id);
                    return ResultT.<Long>builder()
                            .code(HttpStatus.OK.value())
                            .msg("添加成功")
                            .data(id)
                            .build();
                })
                .onErrorResume(e->{
                    log.error("添加sku失败", e);
                    return Mono.just(
                            ResultT.<Long>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("添加失败")
                                    .build());
                });
    }
    //删除SKU
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable Long id) {
        return skuService.deleteById(id)
                .then(Mono.fromCallable(() -> {
                    log.info("删除成功，id为{}",id);
                    return ResultT.<Void>builder()
                            .code(HttpStatus.OK.value())
                            .msg("删除成功")
                            .build();
                }))
                .onErrorResume(throwable ->{
                    log.error("删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("删除失败")
                                    .build());
                });
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<SKUVO>> findById(@PathVariable Long id) {
        return skuService.findById(id)
                .map(sku ->
                        ResultT.<SKUVO>builder()
                                .code(HttpStatus.OK.value())
                                .msg("查询成功")
                                .data(sku)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<SKUVO>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                            .code(HttpStatus.OK.value())
                            .msg("查询成功")
                            .data(pageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<SKUGroupByProductIdVO>>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
    /**
     * 根据商品id获取SKU列表
     * */
    @GetMapping("findByProductId/{productId}")
    public Mono<ResultT<List<SKUVO>>> findByProductId(@PathVariable Long productId) {
        return skuService.findByProductId(productId)
                .collectList()
                .map(skus ->
                        ResultT.<List<SKUVO>>builder()
                                .code(HttpStatus.OK.value())
                                .msg("查询成功")
                                .data(skus)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<List<SKUVO>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
    //批量删除
    @DeleteMapping("deleteAllById")
    public Mono<ResultT<Void>> deleteAllById(@RequestBody List<Long> ids) {
        return skuService.deleteAllById(ids)
                .then(Mono.fromCallable(() -> {
                    log.info("批量删除成功，ids为{}",ids);
                    return ResultT.<Void>builder()
                            .code(HttpStatus.OK.value())
                            .msg("批量删除成功")
                            .build();
                }))
                .onErrorResume(throwable ->{
                            log.error("批量删除失败", throwable);
                            return Mono.just(
                                    ResultT.<Void>builder()
                                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                            .msg("批量删除失败")
                                            .build()
                            );
                        }
                );
    }
    //根据id减库存
    @PutMapping("reduceStockById")
    public Mono<ResultT<Boolean>> reduceStockById(@RequestParam Long id, @RequestParam Integer count) {
        return skuService.reduceStockById(id,count)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("修改库存失败", throwable);
                    return Mono.just(
                            ResultT.<Boolean>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("修改库存失败")
                                    .build()
                    );
                });
    }
    //加 存 库
    @PutMapping("addStockById")
    public Mono<ResultT<Boolean>> addStockById(@RequestParam Long id, @RequestParam Integer count) {
        return skuService.addStockById(id,count)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("修改库存失败", throwable);
                    return Mono.just(
                            ResultT.<Boolean>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("修改库存失败")
                                    .build()
                    );
                });
    }

    //根据ids获取SKU
    @GetMapping("findBySkuIds")
    public Mono<ResultT<List<SKUVO>>> findAllByIds(@RequestParam(value = "skuIds") List<Long> skuIds) {
       return skuService.findAllByIds(skuIds)
               .map(ResultT::success)
               .onErrorResume(e->Mono.just(ResultT.error()));
    }

    //增加销量
    @PutMapping("addSalesById")
    public Mono<ResultT<Boolean>> addSalesById(@RequestParam Long id, @RequestParam Integer count) {
        return skuService.addSalesById(id,count)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("修改销量失败", throwable);
                    return Mono.just(
                            ResultT.<Boolean>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("修改销量失败")
                                    .build()
                    );
                });
    }

    @GetMapping("findTenantIdById/{id}")
    public Mono<ResultT<Long>> findTenantIdById(@PathVariable Long id) {
        return skuService.findTenantIdById(id)
                .map(ResultT::success)
                .onErrorResume(throwable ->{
                    log.error("查询租户ID失败", throwable);
                    return Mono.just(
                            ResultT.<Long>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("查询租户ID失败")
                                    .build()
                    );
                });
    }
}
