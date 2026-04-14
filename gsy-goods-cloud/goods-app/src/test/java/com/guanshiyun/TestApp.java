package com.guanshiyun;

import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.service.sku.SKUService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestApp {
    @Autowired
    private GorseClient gorseClient;

    @Test
    public void test1()
    {

        List<String> block = gorseClient.getRecommend("2")
                .block();
        System.out.println(block);

    }

    @Autowired
    SKUService skuService;
    @Test
    public void test2()
    {
        Long block = skuService.findTenantIdById(3L).block();
        System.out.println("=============");
        System.out.println(block);
    }
}
