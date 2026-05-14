package com.guanshiyun;


import com.db.dbnumber.ConstNumber;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.product.ProductService;
import com.guanshiyun.service.product.RecommendProductService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;

/**
 * Unit test for simple App.
 */
@Slf4j
@SpringBootTest
public class GoodsAppApplicationTest
{

    /**
     *测试对象
     * */
    @Autowired
    private ProductService productService;
    @Test
    public void test()
    {
    }


    @Test
    public void test2()
    {
        productService.findPage(

                RequestPage.<ProductVO>builder()
                        .pageNum(Long.valueOf(1))
                        .pageSize(10)
                        .build()

        ).flatMap(pageResultT -> {
            log.info("查询结果：{}",pageResultT);
            return Mono.just(pageResultT);
        })
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, Long.valueOf(1)))
                .onErrorResume(throwable -> {
                    log.error("分页查询失败：",throwable);
                    return Mono.just(PageResultT.<List<ProductVO>>builder().build());
                })
                .subscribe(i->{
            log.info("查询结果：{}",i);
        });
    }
    @Test
    public void test3()
    {
        productService.deleteById(Long.valueOf(1))
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, Long.valueOf(1)))
                .onErrorResume(throwable -> {
                    log.error("删除失败：",throwable);
                    return Mono.just(ConstNumber.LONG_ZERO);
                })
                .subscribe(i->{
                    log.info("删除成功：{}",i);
                });
    }

    @Autowired
    private RecommendProductService recommendProductService;
    @Test
    public void test4(){

        CursorPageResult<List<ProductCustomerVO>> block = recommendProductService.recommendByPool(10, true)
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, 17L))
                .block();
        List<String> stringStream = block.getRows().stream().map(item -> item.getName()).toList();
        log.info("大模型推荐结果：{}",stringStream);

        List<ProductCustomerVO> block1 = recommendProductService.likePool(1, 10, true)
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, 17L))
                .block();
        List<String> stringStream1 = block1.stream().map(item -> item.getName()).toList();
        log.info("协同推荐结果：{}",stringStream1);
    }


}

