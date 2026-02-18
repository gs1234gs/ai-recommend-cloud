package com.guanshiyun.service.aitool;

import com.guanshiyun.rpc.profile.ProductCustomerApiVO;

import java.math.BigInteger;
import java.util.List;

public interface ProductToolService {
    //根据内容检索获取最相关商品
   List<BigInteger> searchProduct(String content);

   //获取商品列表
   List<ProductCustomerApiVO> toolProductList(List<BigInteger> productList);
}
