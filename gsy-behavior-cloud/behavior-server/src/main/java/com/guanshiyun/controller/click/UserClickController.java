package com.guanshiyun.controller.click;

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
                .map(ResultT::success)
                .onErrorReturn(ResultT.error());
    }
    //查询点击记录
    @GetMapping("findByRows")
    public Mono<ResultT<List<UserClickVO>>> findAll(@RequestParam(required = false) Integer rows){
        return userClickService.findAll(rows)
                .collectList()
                .map(ResultT::success)
                .onErrorReturn(ResultT.error());
    }
}
