package com.xinghe.zmoudle.service;

import com.xinghe.zmoudle.pojo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CollaborativeFilteringService {

    //基于·用户推荐N个商品，topN;
    public List<Product> recommendProducts(Long userId, int topN);

    //基于·商品推荐N个商品
    public List<Product> recommendProductsItemCF(Long userId, int topN);


}
