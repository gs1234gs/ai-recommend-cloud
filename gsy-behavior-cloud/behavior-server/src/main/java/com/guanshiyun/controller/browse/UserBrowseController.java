package com.guanshiyun.controller.browse;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.browse.vo.UserBrowseVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.browse.UserBrowseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/browse/")
@RequiredArgsConstructor
public class UserBrowseController {
    private final UserBrowseService userBrowseService;
    //添加浏览记录

    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody UserBrowseVO userBrowseVO){
        return userBrowseService.save(userBrowseVO)
                .map(id->{
                    log.info("添加成功，id为{}",id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("添加成功")
                            .data(id)
                            .build();
                }).onErrorResume(e->{
                    log.error("添加失败",e);
                    return Mono.just(ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("添加失败")
                            .build()
                    );
                });
    }
    @GetMapping("findByRows")
    public Mono<ResultT<List<UserBrowseVO>>> findByRows(@RequestParam(required = false) Integer rows){
        return userBrowseService.findAll(rows)
                .collectList()
                .map(list->{
                    log.info("查询成功，结果为{}",list);
                    return ResultT. < List<UserBrowseVO >> builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(list)
                            .build();
                })
                .onErrorResume(e->{
                    log.error("查询失败",e);
                    return Mono.just(ResultT.<List<UserBrowseVO>>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("查询失败")
                            .build()
                    );
                })
                .switchIfEmpty(Mono.just(ResultT.<List<UserBrowseVO>>builder()
                        .code(HttpCodeConst.NOT_FOUND)
                        .msg("没有数据")
                                .data(List.of())
                        .build()
                ));
    }
}
