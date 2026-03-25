package com.guanshiyun.controller.collect;

import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.collect.vo.UserCollectSaveVO;
import com.guanshiyun.controller.collect.vo.UserCollectVO;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.collect.UserCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/collect/")
@RequiredArgsConstructor
public class UserCollectController {
    private final UserCollectService userCollectService;

    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody UserCollectSaveVO userCollectSaveVO) {
        return userCollectService.save(userCollectSaveVO)
                .map(ResultT::success)
                .onErrorReturn(
                        ResultT.<Long>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("保存失败")
                                .build()
                );
    }

    @GetMapping("findByRows")
    public Mono<ResultT<List<UserCollectVO>>> findByRows(@RequestParam(required = false) Integer rows) {
        return userCollectService.findAll(rows)
                .collectList()
                .map(ResultT::success)
                .onErrorReturn(ResultT.<List<UserCollectVO>>builder()
                                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                .msg("查询失败")
                                .build()
                );
    }
    //删除收藏记录
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Boolean>> deleteById(@PathVariable Long id) {
        try {
            return userCollectService.deleteById(id)
                    .then(Mono.fromCallable(() -> ResultT.success(Boolean.TRUE)));
        } catch (Exception e) {
            log.error("删除收藏记录失败", e);
            return Mono.just(ResultT.error("删除失败", Boolean.FALSE));
        }
    }

    //分页
    @GetMapping("findByPage")
    public Mono<ResultT<PageResultT<List<UserCollectVO>>>> findByPage(@RequestParam (defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {

        return userCollectService.findByPage(pageNum, pageSize)
                .map(ResultT::success);
    }
}
