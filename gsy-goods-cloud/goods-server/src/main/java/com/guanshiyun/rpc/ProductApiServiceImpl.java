//package com.guanshiyun.rpc;
//
//import com.guanshiyun.requestpojo.RequestCursorPage;
//import com.guanshiyun.responsepojo.CursorPageResult;
//import com.guanshiyun.responsepojo.ResultT;
//import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
//import com.guanshiyun.rpc.profile.ProductApiVO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//@RestController
//@RequiredArgsConstructor
//public class ProductApiServiceImpl implements ProductApiService {
//    private final WebClient webClient;
//    @Override
//    public Mono<ResultT<CursorPageResult<List<ProductApiVO>>>> findCursor(RequestCursorPage<ProductApiVO> requestCursorPage) {
//        return  webClient
//                                .post()
//                                .bodyValue(requestCursorPage)
//                                .retrieve()
//                                .bodyToMono(new ParameterizedTypeReference<>() {});
//    }
//}
