package com.guanshiyun.controller.model;

import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.model.BigModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bigModel/")
@RequiredArgsConstructor
public class BigModelController {
    private final BigModelService bigModelService;

    //添加大模型,修改大模型
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody BigModel bigModel) {
        return bigModelService.sava(bigModel)
                .map(saveId ->
                        ResultT.<BigInteger>builder()
                                .code(HttpCodeConst.OK)
                                .data(saveId)
                                .build());
    }

    //删除大模型
    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<BigInteger>> delete(@PathVariable BigInteger id) {
        return bigModelService.deleteById(id)
                .map(deleteCount -> {
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .data(deleteCount)
                            .build();
                }).onErrorResume(throwable -> {
                    log.error("删除大模型失败", throwable);
                    return Mono.just(ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .data(BigInteger.ZERO)
                            .build());
                });
    }

    //查询大模型
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<BigModel>>>> query(@RequestBody RequestPage<BigModel> requestPage) {
        return bigModelService.findPage(requestPage)
                .map(pageResultT ->
                        ResultT.<PageResultT<List<BigModel>>>builder()
                                .code(HttpCodeConst.OK)
                                .data(pageResultT)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询大模型失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<BigModel>>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .data(null)
                                    .build()
                    );
                });
    }

    //
    @GetMapping("findById/{id}")
    public Mono<ResultT<BigModel>> queryById(@PathVariable BigInteger id) {
        return bigModelService.findById(id)
                .map(bigModel ->
                        ResultT.<BigModel>builder()
                                .code(HttpCodeConst.OK)
                                .data(bigModel)
                                .build()
                        )
                .switchIfEmpty(
                        Mono.just(ResultT.<BigModel>builder()
                                .code(HttpCodeConst.NOT_FOUND)
                                .data(null)
                                .build())
                )
                .onErrorResume(throwable -> {
                    log.error("查询大模型失败", throwable);
                    return Mono.just(ResultT.<BigModel>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .build());
                });
    }
}
