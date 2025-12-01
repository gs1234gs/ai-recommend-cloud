package com.xinghe.zmoudle.dto;

import com.xinghe.zmoudle.pojo.Product;
import com.xinghe.zmoudle.pojo.SysUser;

import java.util.List;
import java.util.Map;

public interface PurchaseDO {
    /**
     *-------------------基于用户的协同过滤-------------------
     * */
    //获取用户所有购买记录
    Map<Long,Integer> getUserPurchaseRecord(Long userId);
    //获取所有用户数据
    List<SysUser> getAllUser();
    //获取所有商品数据
    List<Product> getAllProduct();
    /**
     *-------------------基于商品的协同过滤-------------------
     * */
    //获取商品所有购买记录
    Map<Long,Integer> getProductPurchaseRecord(Long productId);
}
