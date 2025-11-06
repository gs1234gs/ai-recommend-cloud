package com.xinghe.zmoudle.service;

import com.xinghe.zmoudle.pojo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CollaborativeFilteringService {

    //对当前用户推荐N个商品，topN;
    public List<Product> recommendProducts(Long userId, int topN);


}
