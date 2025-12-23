package com.guanshiyun.controller.search;

import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.search.vo.UserSearchSaveVO;
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
    public Mono<ResultT<BigInteger>> save(@RequestBody UserSearchSaveVO userSearchVO) {
        return userSearchService.save(userSearchVO)
                .map(ResultT::success)
                .onErrorResume(e -> Mono.just(ResultT
                                .<BigInteger>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("保存失败")
                                .build()
                        )
                );
    }

    @GetMapping("findByRaws")
    public Mono<ResultT<List<UserSearchVO>>> findByRaws(@RequestParam(required = false) Integer rows) {
        return userSearchService.findAll(rows)
                .collectList()
                .map(ResultT::success)
                .onErrorResume(e -> Mono.just(ResultT
                        .<List<UserSearchVO>>builder()
                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                        .msg("查询失败")
                        .build())

                );
    }
}
