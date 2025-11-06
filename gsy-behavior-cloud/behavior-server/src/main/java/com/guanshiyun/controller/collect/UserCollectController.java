package com.guanshiyun.controller.collect;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.collect.UserCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/collect/")
@RequiredArgsConstructor
public class UserCollectController {
    private final UserCollectService userCollectService;

    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody UserCollectVO userCollectVO){
        return userCollectService.save(userCollectVO)
                .map(id->{
                    log.info("保存成功，id为{}",id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("保存成功")
                            .data(id)
                            .build();

                })
                .onErrorResume(e->{
                    log.error("保存失败",e);
                    return Mono.just(ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("保存失败")
                            .build());
                });
    }
    @GetMapping("findByRaws")
    public Mono<ResultT<List<UserCollectVO>>> findByRaws(@RequestParam(required = false) Integer rows){
        return userCollectService.findAll(rows)
                .collectList()
                .map(list->{
                    log.info("查询成功，结果为{}",list);
                    return ResultT.<List<UserCollectVO>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(list)
                            .build();
                })
                .onErrorResume(e->{
                    log.error("查询失败",e);
                    return Mono.just(ResultT.<List<UserCollectVO>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("查询失败")
                            .build());
                });
    }
}
