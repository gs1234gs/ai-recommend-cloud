package com.guanshiyun;

import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.profile.ProductApiVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
public class TestGoods {
    @Autowired
    private  ProductApiService productApiService;
    @Test
    void test() {
        productApiService.findCursor(
                        RequestCursorPage.<ProductApiVO>builder().pageSize(100).build()
                )
                .doOnSubscribe(subscription -> System.out.println("Subscription started"))
                .map(result -> result.getData().getRows())
                .doOnNext(rows -> System.out.println("Fetched rows: " + rows.size()))
                .doOnTerminate(() -> System.out.println("Process finished"))
                .block(Duration.ofSeconds(10)); // 设置超时

    }
}
