package com.xinghe.zmoudle.dto.impl;

import com.xinghe.zmoudle.dto.PurchaseDO;
import com.xinghe.zmoudle.pojo.Product;
import com.xinghe.zmoudle.pojo.SysUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseDOImpl implements PurchaseDO{

    //模拟该买记录数据库
    private static final Map<Long,Map<Long,Integer>> PURCHASE_DB = new HashMap<>();
    //模拟商品数据库
    private static final List<Product> PRODUCT_DB = new ArrayList<>();
    //模拟用户数据库
    private static final List<SysUser> USER_DB = new ArrayList<>();

    //关于商品维度的数据查询
    public static final Map<Long,Map<Long,Integer>> PURCHASE_DB_PRODUCT = new HashMap<>();
    static {
        //初始化：mysql 模拟数据库；模拟用户、商品、用户购买记录
        USER_DB.add(SysUser.builder()
                .id(1L)
                .username("zhaolusi")
                .nickName("赵路思").build()
        );
        USER_DB.add(SysUser.builder()
                .id(2L)
                .username("liuyifei")
                .nickName("刘亦菲").build()
        );
        USER_DB.add(SysUser.builder()
                .id(3L)
                .username("huangxiaoming")
                .nickName("黄晓明").build()
        );
        //商品
        PRODUCT_DB.add(Product.builder()
                .id(100L)
                .name("Iphone 17")
                .price(new BigDecimal("8888.00"))
                .build()
        );
        PRODUCT_DB.add(Product.builder()
                .id(101L)
                .name("vivo x100 pro")
                .price(new BigDecimal("8848.00"))
                .build()
        );
        PRODUCT_DB.add(Product.builder()
                .id(102L)
                .name("联想 i 9")
                .price(new BigDecimal("9999.00"))
                .build()
        );
        //购买记录
        //zhaolusi 购买了 Iphone 17 3个，zhaolusi 购买了 vivo x100 pro 2个
        PURCHASE_DB.put(1L,Map.of(100L,3,101L,2));
        //zhaolusi 购买了 联想 i 9 1个,
        PURCHASE_DB.put(2L,Map.of(100L,1,102L,4));
        //zhaolusi 购买了 联想 i 9 1个,
        PURCHASE_DB.put(3L,Map.of(101L,2,102L,4));

        //商品维度的
        PURCHASE_DB_PRODUCT.forEach((userId,purchase)->{
            purchase.forEach((productId,count)->{
                PURCHASE_DB_PRODUCT.computeIfAbsent(productId,k->new HashMap<>())
                        .put(userId,count);
            });
        });
    }

    @Override
    public Map<Long, Integer> getUserPurchaseRecord(Long userId) {
        return PURCHASE_DB.getOrDefault(userId,Map.of());
    }

    @Override
    public List<SysUser> getAllUser() {
        return USER_DB;
    }

    @Override
    public List<Product> getAllProduct() {
        return PRODUCT_DB;
    }

    @Override
    public Map<Long, Integer> getProductPurchaseRecord(Long productId) {
        return PURCHASE_DB_PRODUCT.getOrDefault(productId,Map.of());
    }
}
