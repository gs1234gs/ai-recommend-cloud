package com.guanshiyun.service.aitool;

import com.guanshiyun.profile.ProductCustomerApiVO;


import java.util.List;

public interface ProductToolService {
    //根据内容检索获取最相关商品
   List<Long> searchProduct(String content);

   //获取商品列表
   List<ProductCustomerApiVO> toolProductList(List<Long> productList);
}
