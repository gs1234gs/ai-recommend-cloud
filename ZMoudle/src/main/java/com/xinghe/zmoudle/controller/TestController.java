package com.xinghe.zmoudle.controller;

import com.xinghe.zmoudle.pojo.Product;
import com.xinghe.zmoudle.service.CollaborativeFilteringServiceImpl;

import java.util.List;

public class TestController {
    public static void main(String[] args) {
        CollaborativeFilteringServiceImpl cv = new CollaborativeFilteringServiceImpl();
        List<Product> products = cv.recommendProducts(2L, 2);
        products.forEach(System.out::println);
    }
}
