package com.guanshiyun.controller.tag;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.tag.vo.TagSaveVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tag/")
@RestController
public class TagController {
    private final TagService tagService;

    //添加标签
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody TagSaveVO tagSaveVO) {
        return tagService.save(tagSaveVO)
                .map(id -> {
                    log.info("添加成功，id为{}", id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("添加成功")
                            .data(id)
                            .build();
                })
                .onErrorResume(throwable -> {
                            log.error("添加失败", throwable);
                            return Mono.just(
                                    ResultT.<BigInteger>builder()
                                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                            .msg("添加失败")
                                            .build()
                            );
                        }
                );
    }

    //删除标签
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return tagService.deleteById(id)
                .then(Mono.just(
                        ResultT.<Void>builder()
                                .code(HttpCodeConst.OK)
                                .msg("删除成功")
                                .build()
                ))
                .onErrorResume(throwable ->{
                    log.error("删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除失败")
                                    .build()
                    );
                });
    }

    @GetMapping("findById/{id}")
    public Mono<ResultT<TagVO>> findById(@PathVariable BigInteger id) {
      return   tagService.findById( id)
                .map(tag ->{
                    log.info("查询成功，id为{}", id);
                    return ResultT.<TagVO>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(tag)
                            .build();
                });
    }

    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<TagVO>>>> findAllByPage(@RequestBody RequestPage<TagVO> requestPage) {
        return tagService.findAllByPage(requestPage)
                .map(pageResultT ->{
                    log.info("查询成功");
                    return ResultT.<PageResultT<List<TagVO>>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(pageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<TagVO>>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
    //批量删除
    @DeleteMapping("deleteAllById")
    public Mono<ResultT<Void>> deleteByIds(@RequestBody List<BigInteger> ids) {
        return tagService.deleteAllById(ids)
                .then(Mono.just(
                        ResultT.<Void>builder()
                                .code(HttpCodeConst.OK)
                                .msg("删除成功")
                                .build()
                ))
                .onErrorResume(throwable ->{
                    log.error("批量删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("批量删除失败")
                                    .build()
                    );
                });
    }
}
