package com.guanshiyun.controller.product;

import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RequestMapping("/product")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    //添加商品
    @PostMapping("/save")
    public Mono<ResultT<BigInteger>> save(@RequestBody ProductSaveVO productSaveVO){
        return productService.save(productSaveVO)
                .map(productId->
                {
                    log.info("保存商品成功，商品ID为：{}",productId);
                    return ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.OK)
                            .msg("保存成功")
                            .data(productId)
                            .build();
                }).switchIfEmpty(
                        Mono.fromCallable(()->
                                {
                                    log.info("保存商品失败");
                                    return ResultT.<BigInteger>builder()
                                            .code(HttpCodeConst.BAD_REQUEST)
                                            .msg("保存失败")
                                            .build();
                                }
                                )
                )
                .onErrorResume(throwable ->{
                    log.info("保存商品失败", throwable);
                    return Mono.just(ResultT.<BigInteger>builder()
                            .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                            .msg("保存失败")
                            .build());
                });
    }
    /**
     * 删除商品
     * */
    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<Long>> deleteById(@PathVariable BigInteger id){
        return productService.deleteById(id)
                .map(deleteCount->{
                    log.info("删除商品成功，删除数量为：{}", deleteCount);
                  return   ResultT.<Long>builder()
                            .code(HttpCodeConst.OK)
                            .msg("删除成功")
                            .data(deleteCount)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("删除商品失败", throwable);
                    return Mono.just(
                            ResultT.<Long>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除失败")
                                    .build()
                    );
                });
    }
    /**
     * 分页查询，返回管理端端商品列表
     * */
   @PostMapping("/findPage")
   public Mono<PageResultT<List<ProductVO>>> findPage(@RequestBody RequestPage<ProductVO> requestPage){
      return productService.findPage(requestPage);
   }
   /**
    * 游标查询，返回客户端
    * */
   @PostMapping("/findCursor")
    public Mono<ResultT<CursorPageResult<List<ProductCustomerVO>>>>  findCursor(@RequestBody RequestCursorPage<ProductVO> requestCursorPage){
       return productService.findCursor(requestCursorPage)
               .map(cursorPageResult ->
                       ResultT.<CursorPageResult<List<ProductCustomerVO>>>builder()
                               .code(HttpCodeConst.OK)
                               .msg("查询成功")
                               .data(cursorPageResult)
                               .build()

                       )
               .onErrorResume(throwable ->{
                   log.info("查询失败", throwable);
                   return Mono.just(
                           ResultT.<CursorPageResult<List<ProductCustomerVO>>>builder()
                                   .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                   .msg("系统错误")
                                   .build()
                   );
               });
   }
   //批量删除
    @DeleteMapping("/deleteAllById")
    public Mono<ResultT<Void>> deleteAllById(@RequestBody List<BigInteger> ids){
        return productService.deleteAllById(ids)
                .map(deleteCount->
                        ResultT.<Void>builder()
                                .code(HttpCodeConst.OK)
                                .msg("删除成功")
                                .data(deleteCount)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.info("删除失败", throwable);
                    return Mono.just(
                            ResultT.<Void>builder()
                                    .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                    .msg("删除失败")
                                    .build()
                    );
                });
    }
    @GetMapping("/findById/{id}")
    public Mono<ResultT<ProductVO>> findById(@PathVariable BigInteger id){
       return productService.findById(id)
               .map(productVO ->
                       ResultT.<ProductVO>builder()
                               .code(HttpCodeConst.OK)
                               .msg("查询成功")
                               .data(productVO)
                               .build()
               )
               .onErrorResume(throwable ->{
                   log.info("查询失败", throwable);
                   return Mono.just(
                           ResultT.<ProductVO>builder()
                                   .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                   .msg("查询失败")
                                   .build()
                   );
               });
    }
}
