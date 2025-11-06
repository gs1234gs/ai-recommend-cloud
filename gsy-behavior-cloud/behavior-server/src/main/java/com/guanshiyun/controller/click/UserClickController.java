package com.guanshiyun.controller.click;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.click.vo.UserClickVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.click.UserClickService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/click/")
@RequiredArgsConstructor
public class UserClickController {
    private final UserClickService userClickService;
    //保存点击记录
    @RequestMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody UserClickVO userClickVO){
        return userClickService.save(userClickVO)
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
    //查询点击记录
    @GetMapping("findByRows")
    public Mono<ResultT<List<UserClickVO>>> findAll(@RequestParam(required = false) Integer rows){
        return userClickService.findAll(rows)
                .collectList()
                .map(userClickVO -> {
                    log.info("查询成功");
                    return ResultT.<List<UserClickVO>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(userClickVO)
                            .build();
                })
                .onErrorResume(e->{
                    log.error("查询失败",e);
                    return Mono.just(
                            ResultT.<List<UserClickVO>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
}
