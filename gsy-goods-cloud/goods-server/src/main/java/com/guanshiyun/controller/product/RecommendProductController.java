package com.guanshiyun.controller.product;

import com.guanshiyun.controller.product.vo.ProductCustomerDetailVO;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSearchSaveVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.product.RecommendProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

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
                .map(ResultT::success);
    }
}
