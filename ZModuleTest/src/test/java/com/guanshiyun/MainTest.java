package com.guanshiyun;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class MainTest {

    @Test
    void contextLoads() {

    }
    public static void main(String[] args) {
//        System.out.println(1>>2);
//        System.out.println(2<<1);
//        System.out.println(~4);
//        if((~1>2) ^ (2>~3))
//            System.out.println("1");
        int [] arr = {2,1,3,4,9,5,7,6,8,10};
        int[] array = Arrays.stream(arr).sorted().toArray();

    }



}
