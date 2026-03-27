package com.guanshiyun.controller.model;

import com.db.dbnumber.ConstNumber;
import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.model.BigModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bigModel/")
@RequiredArgsConstructor
public class BigModelController {
    private final BigModelService bigModelService;

    //添加大模型,修改大模型
    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody BigModel bigModel) {
        return bigModelService.sava(bigModel)
                .map(saveId ->
                        ResultT.<Long>builder()
                                .code(HttpStatus.OK.value())
                                .data(saveId)
                                .build());
    }

    //删除大模型
    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<Long>> delete(@PathVariable Long id) {
        return bigModelService.deleteById(id)
                .map(deleteCount -> {
                    return ResultT.<Long>builder()
                            .code(HttpStatus.OK.value())
                            .data(deleteCount)
                            .build();
                }).onErrorResume(throwable -> {
                    log.error("删除大模型失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .data(ConstNumber.LONG_ZERO)
                            .build());
                });
    }

    //查询大模型
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<BigModel>>>> query(@RequestBody RequestPage<BigModel> requestPage) {
        return bigModelService.findPage(requestPage)
                .map(pageResultT ->
                        ResultT.<PageResultT<List<BigModel>>>builder()
                                .code(HttpStatus.OK.value())
                                .data(pageResultT)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("查询大模型失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<BigModel>>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .data(null)
                                    .build()
                    );
                });
    }

    //
    @GetMapping("findById/{id}")
    public Mono<ResultT<BigModel>> queryById(@PathVariable Long id) {
        return bigModelService.findById(id)
                .map(bigModel ->
                        ResultT.<BigModel>builder()
                                .code(HttpStatus.OK.value())
                                .data(bigModel)
                                .build()
                        )
                .switchIfEmpty(
                        Mono.just(ResultT.<BigModel>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .data(null)
                                .build())
                )
                .onErrorResume(throwable -> {
                    log.error("查询大模型失败", throwable);
                    return Mono.just(ResultT.<BigModel>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .data(null)
                            .build());
                });
    }
}
