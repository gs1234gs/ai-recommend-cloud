package com.guanshiyun;

import com.guanshiyun.goser.GorseClient;
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

        List<String> block = gorseClient.getRecommend("1")
                .block();
        System.out.println(block);

    }
}
