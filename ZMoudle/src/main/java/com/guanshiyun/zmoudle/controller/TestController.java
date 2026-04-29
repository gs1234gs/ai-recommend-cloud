package com.guanshiyun.zmoudle.controller;

import com.guanshiyun.zmoudle.pojo.Product;
import com.guanshiyun.zmoudle.service.CollaborativeFilteringServiceImpl;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class TestController {
    public static void main(String[] args) {
        CollaborativeFilteringServiceImpl cv = new CollaborativeFilteringServiceImpl();
        List<Product> products = cv.recommendProducts(2L, 2);
        products.forEach(System.out::println);
    }
}
