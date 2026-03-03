package com.guanshiyun.controller.product;

import com.guanshiyun.controller.product.vo.ProductCustomerDetailVO;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSearchSaveVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.product.RecommendProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendProduct")
public class RecommendProductController {

    private final RecommendProductService recommendProductService;

    //搜索
    @PostMapping("/search")
    public Mono<ResultT<CursorPageResult<List<ProductCustomerVO>>>> searchProduct(@RequestBody RequestCursorPage<ProductSearchSaveVO> requestCursorPage){
       return recommendProductService.findCursor(requestCursorPage)
                .map(ResultT::success);
    }
    //推荐
    @GetMapping("/recommend")
    public Mono<ResultT<List<ProductCustomerVO>>> recommend(){
        return recommendProductService.recommend()
                .map(ResultT::success);
    }
    //猜你喜欢
    @GetMapping("/like")
    public Mono<ResultT<List<ProductCustomerVO>>> like(){
        return recommendProductService.like()
                .map(ResultT::success);
    }
//    //获取商品详情
    @GetMapping("/detail/{id}")
    public Mono<ResultT<ProductCustomerDetailVO>> detail(@PathVariable BigInteger id){
        return recommendProductService.detail(id)
                .map(ResultT::success);
    }

    //根据推荐商品id列表获取商品
    @GetMapping("/recommendByIds")
    public Mono<ResultT<List<ProductCustomerVO>>> recommendByIds(@RequestParam List<BigInteger> ids ){
        return recommendProductService.findByIds(ids)
                .map(ResultT::success)
                .onErrorResume(e -> {
                    log.error("获取商品详情失败：",e);
                    return Mono.just(ResultT.error(e.getMessage()));
                });
    }

    /**
     * 推荐商品滚动加载（基于 Redis 候选池）
     */
    @GetMapping("/recommendScroll")
    public Mono<ResultT<CursorPageResult<List<ProductCustomerVO>>>> recommendScroll(
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "refresh", required = false, defaultValue = "false") Boolean refresh
    ) {
        return recommendProductService.recommendByPool(pageSize, refresh)
                .map(ResultT::success);
    }
    //猜你喜欢滚动加载（基于 Redis 候选池）
    @GetMapping("/likeScroll")
    public Mono<ResultT<List<ProductCustomerVO>>> likeScroll(
            @RequestParam (value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "refresh", required = false, defaultValue = "false") Boolean refresh
    ) {
        return recommendProductService.likePool(offset, pageSize, refresh)
                .map(ResultT::success);
    }
    //热门
    @GetMapping("/hot")
    public Mono<ResultT<List<ProductCustomerVO>>> hot(){
        return recommendProductService.hot()
                .map(ResultT::success);
    }
    //最新上架
    @GetMapping("/mostNew")
    public Mono<ResultT<List<ProductCustomerVO>>> mostNew(){
        return recommendProductService.mostNew()
                .map(ResultT::success);
    }
    //游标分页
    @PostMapping("/findCursorEnd")
    public Mono<ResultT<CursorPageResult<List<ProductCustomerVO>>>> findCursor(@RequestBody RequestCursorPage<ProductCustomerVO> requestCursorPage){
        return recommendProductService.findCursorEnd(requestCursorPage)
                .map(ResultT::success);
    }
}
