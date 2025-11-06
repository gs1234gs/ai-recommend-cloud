package com.xinghe.zmoudle.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ServiceTest {

    public void collaborativeFiltering(BigInteger userId, int topN) {
        //1、获取搜有的用户、商品的数据
        // 2、计算用户之间的相似度
        //3、获取相似的K个用户，比如说前几个排名靠前的相似
        //4、根据计算出来的用户，去取不相购买交集的商品
        //5、推荐商品、返回数据 controller
    }

}
