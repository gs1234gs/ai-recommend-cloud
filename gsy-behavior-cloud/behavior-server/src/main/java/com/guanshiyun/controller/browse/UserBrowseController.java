package com.guanshiyun.controller.browse;

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
    public Mono<ResultT<BigInteger>> save(@RequestBody UserBrowseVO userBrowseVO) {
        return userBrowseService.save(userBrowseVO)
                .map(ResultT::success)
                .onErrorReturn(ResultT.error());
    }

    @GetMapping("findByRows")
    public Mono<ResultT<List<UserBrowseVO>>> findByRows(@RequestParam(required = false) Integer rows) {
        return userBrowseService.findAll(rows)
                .collectList()
                .map(ResultT::success)
                .onErrorReturn(ResultT.error());
    }
}
