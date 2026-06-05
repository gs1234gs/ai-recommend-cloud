package com.guanshiyun;

import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.review.ReviewProduct;
import com.guanshiyun.service.review.ReviewProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class ReviewAppApplicationTest

{
    @Autowired
    ReviewProductService reviewProductService;


    @Test
    public void contextLoads()
    {
        Object content = reviewProductService.save(ReviewProduct
                        .builder()
                        .id(1L)
                        .content("content")
                        .tenantId(1L)
                        .creator(1L)
                        .image("image")
                        .productId(1L)
                        .parentId(0L)
                        .createTime(LocalDateTime.now())
                        .build()
                )
                .block();
        System.out.println("===============================================");
        System.out.println(content);
    }
    @Test
    public void test()
    {
        PageResultT<List<ReviewProduct>> content = reviewProductService.list(0L, 10, "content").block();
        System.out.println("===============================================");
        System.out.println(content);
    }

}
