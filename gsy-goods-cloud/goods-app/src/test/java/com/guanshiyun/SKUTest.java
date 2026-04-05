package com.guanshiyun;

import com.guanshiyun.repository.sku.SKURepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SKUTest {
    @Autowired
    SKURepository skuRepository;
    @Test
    public void test(){
        Long result = skuRepository.countTotalSales().block();
        Long block = skuRepository.countTotalStock().block();

        System.out.println("==================");
        System.out.println(result);
        System.out.println("block: "+block);
    }
}
