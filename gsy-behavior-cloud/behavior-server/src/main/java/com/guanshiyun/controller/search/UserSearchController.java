package com.guanshiyun.controller.search;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.search.vo.UserSearchVO;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.search.UserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/search/")
@RestController
public class UserSearchController {
    private final UserSearchService userSearchService;

    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody UserSearchVO userSearchVO){
        return userSearchService.save(userSearchVO)
                .map(id->{
                    log.info("保存成功，id为{}",id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("保存成功")
                            .data(id)
                            .build();
                }).onErrorResume(e->{
                    log.error("保存失败",e);
                    return Mono.just(ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("保存失败")
                            .build()
                    );
                });
    }
    @GetMapping("findByRaws")
    public Mono<ResultT<List<UserSearchVO>>> findByRaws(@RequestParam(required = false) Integer rows){
        return userSearchService.findAll(rows)
                .collectList()
                .map(userSearchVO -> {
                    log.info("查询成功，结果为{}",userSearchVO);
                    return ResultT.<List<UserSearchVO>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(userSearchVO)
                            .build();
                }).onErrorResume(
                        e -> {
                            log.error("查询失败",e);
                            return Mono.just(
                                    ResultT.<List<UserSearchVO>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                            );
                        }
                );
    }
}
