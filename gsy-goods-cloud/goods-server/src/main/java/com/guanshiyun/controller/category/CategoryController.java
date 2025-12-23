package com.guanshiyun.controller.category;

import com.guanshiyun.category.Category;
import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.controller.category.vo.CategorySaveVO;
import com.guanshiyun.controller.category.vo.CategoryVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/category/")
@RestController
public class CategoryController {
    private final CategoryService categoryService;
    //添加类型或修改
    @PostMapping("save")
    public Mono<ResultT<BigInteger>> save(@RequestBody CategorySaveVO categorySaveVO) {
        return categoryService.save(categorySaveVO)
                .map(id->{
                    log.info("添加成功，id为{}",id);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("添加成功")
                            .data(id)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.error("添加失败", throwable);
                    return Mono.just(
                            ResultT.<BigInteger>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }
    //删除类型
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Void>> deleteById(@PathVariable BigInteger id) {
        return categoryService.deleteById(id)
                .then(Mono.fromCallable(() -> {
                    log.info("删除成功，id为{}",id);
                    return ResultT.<Void>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除成功")
                            .build();
                }))
                .onErrorResume(throwable ->{
                    log.error("删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }
    //根据id获取
    @GetMapping("findById/{id}")
    public Mono<ResultT<Category>> fndById(@PathVariable BigInteger id) {
        return categoryService.fndById(id)
                .map(categorySaveVO ->{
                    log.info("查询成功，id为{}",id);
                    return ResultT.<Category>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(categorySaveVO)
                            .build();
                }).onErrorResume(throwable ->{
                    log.error("查询失败", throwable);
                    return Mono.just(
                            ResultT.<Category>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }
    //查询类型
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<CategoryVO>>>> findAllByPage(@RequestBody (required = false) RequestPage<CategoryVO> requestPage) {
        return categoryService.findAllByPage(requestPage)
                .map(pageResultT ->{
                    log.info("查询成功");
                    return ResultT.<PageResultT<List<CategoryVO>>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(pageResultT)
                            .build();
                }).onErrorResume(e->{
                    log.error("查询失败", e);
                    return Mono.just(
                            ResultT.<PageResultT<List<CategoryVO>>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }

    @GetMapping("findAll")
    public Mono<ResultT<List<CategoryVO>>> findAll(){
        return categoryService.findAll()
                .map(category ->{
                    log.info("查询成功");
                    return ResultT.<List<CategoryVO>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(category)
                            .build();
                }).onErrorResume(e->{
                    log.error("查询失败", e);
                    return Mono.just(
                            ResultT.<List<CategoryVO>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }
    @GetMapping("findByProductId/{productId}")
    public Mono<ResultT<List<CategoryVO>>> findByProductId(@PathVariable BigInteger productId){
        return categoryService.findByProductId(productId)
                .collectList()
                .map(category ->{
                    log.info("查询成功");
                    return ResultT.<List<CategoryVO>>builder()
                            .code(HttpCodeConst.OK)
                            .msg("查询成功")
                            .data(category)
                            .build();
                }).onErrorResume(e->{
                    log.error("查询失败", e);
                    return Mono.just(
                            ResultT.<List<CategoryVO>>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("系统错误")
                                    .build()
                    );
                });
    }

}
