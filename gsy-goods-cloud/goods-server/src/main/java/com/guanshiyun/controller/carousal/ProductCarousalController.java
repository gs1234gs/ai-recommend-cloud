package com.guanshiyun.controller.carousal;

import com.guanshiyun.controller.carousal.vo.ProductCarousalSaveVO;
import com.guanshiyun.controller.carousal.vo.ProductCarousalVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.carousal.ProductCarousalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@RestController()
@RequestMapping("/product/carousal")
@RequiredArgsConstructor
public class ProductCarousalController {
    private final ProductCarousalService productCarousalService;
    //保存轮播图
    @PostMapping("/save")
    public Mono<ResultT<ProductCarousalVO>> saveProductCarousal(@RequestBody ProductCarousalSaveVO productCarousalSaveVO) {
        return productCarousalService.save(productCarousalSaveVO)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("操作失败：" , throwable);
                          return   Mono.just(ResultT
                                    .<ProductCarousalVO>builder()
                                    .msg("操作失败：" + throwable.getMessage())
                                    .build());
                        }
                );
    }
    //获取轮播图
    @GetMapping("/findAll")
    public Mono<ResultT<List<ProductCarousalVO>>> findAll() {
        return productCarousalService.findAll()
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT
                        .<List<ProductCarousalVO>>builder()
                        .msg("操作失败：" + throwable.getMessage())
                        .build())
                );
    }
    //删除轮播图
    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return productCarousalService.deleteById(id)
                .then(Mono.just(ResultT.<Void>success()))
                .onErrorResume(throwable -> Mono.just(ResultT
                        .<Void>builder()
                        .msg("操作失败：" + throwable.getMessage())
                        .build())
                );
    }
    //批量删除轮播图
    @DeleteMapping("/deleteBatch")
    public Mono<ResultT<Void>> deleteBatch(@RequestBody List<BigInteger> ids) {
        return productCarousalService.deleteByIds(ids)
                .then(Mono.just(ResultT.<Void>success()))
                .onErrorResume(e->{
                    log.error("操作失败：" , e);
                    return Mono.just(ResultT
                            .<Void>builder()
                            .msg("操作失败：" + e.getMessage())
                            .build());
                        }
                );
    }
    @GetMapping("/findById/{id}")
    public Mono<ResultT<ProductCarousalVO>> findById(@PathVariable BigInteger id) {
        return productCarousalService.findById(id)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT
                        .<ProductCarousalVO>builder()
                        .msg("操作失败：" + throwable.getMessage())
                        .build())
                );
    }
    @GetMapping("/findByType/{type}")
    public Mono<ResultT<List<ProductCarousalVO>>> findByType(@PathVariable Integer type) {
        return productCarousalService.findByType(type)
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT
                        .<List<ProductCarousalVO>>builder()
                        .msg("操作失败：" + throwable.getMessage())
                        .build())
                );
    }
}
