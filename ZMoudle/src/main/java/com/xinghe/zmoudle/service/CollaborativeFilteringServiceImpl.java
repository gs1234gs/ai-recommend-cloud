package com.xinghe.zmoudle.service;

import com.xinghe.zmoudle.dto.PurchaseDO;
import com.xinghe.zmoudle.dto.impl.PurchaseDOImpl;
import com.xinghe.zmoudle.pojo.Product;
import com.xinghe.zmoudle.pojo.SysUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollaborativeFilteringServiceImpl implements CollaborativeFilteringService{
     PurchaseDO purchase  = new PurchaseDOImpl();

    @Override
    public List<Product> recommendProducts(Long userId, int topN) {
        //1、获取所有的用户
        List<SysUser> allUser = purchase.getAllUser();
        //2、获取所有的商品数据
        List<Product> allProduct = purchase.getAllProduct();
        //当前用户的购买记录
        Map<Long, Integer> currentUserPurchaseRecord = purchase.getUserPurchaseRecord(userId);
        // 2、计算用户之间的相似度
       Map<Long, Double> similarity = new HashMap<>();
        allUser.stream()
                .filter(user->!user.getId().equals(userId))
                .forEach(user->{
                    //其他用户的购买记录
                    Map<Long, Integer> otherUserPurchaseRecord = purchase.getUserPurchaseRecord(user.getId());
                    //计算相似度
                    double v = collaborativeFiltering(currentUserPurchaseRecord, otherUserPurchaseRecord);
                    //保存
                    similarity.put(user.getId(), v);
                });
        //3、获取相似的K个用户，比如说前几个排名靠前的相似
        int k =2;
        List<Long> topList = similarity.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .map(Map.Entry::getKey)
                .toList();
        //4、根据计算出来的用户，去取不相购买交集的商品
        Map<Long, Integer> productSources = new HashMap<>();
        topList.forEach(userIds->{
            Map<Long, Integer> userPurchaseRecord = purchase.getUserPurchaseRecord(userIds);
            userPurchaseRecord.forEach((productId, count)->{
                if(!currentUserPurchaseRecord.containsKey(productId)){
                    productSources.merge(productId, count, Integer::sum);
                }
            });
        });
        //推=推荐topN的商品，优先推荐相似度高的商品,推荐商品、返回数据 controller
        return productSources.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(entry->{
                    return allProduct.stream()
                            .filter(p->p.getId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                })
                .toList();
    }
    //

    /**
     *
     * 计算余弦相似度
     * */
    public double collaborativeFiltering(Map<Long, Integer> user1, Map<Long, Integer> user2) {
        //计算相似度
        Set<Long> commonProducts = new HashSet<>(user1.keySet());
        commonProducts.retainAll(user2.keySet());
        double dotProduct = commonProducts.stream().mapToDouble(productId ->
                Math.fma(user1.get(productId), user2.get(productId), 0.0)
//                user1.get(productId) * user2.get(productId)
        ).sum();
        double sqrt1 = Math.sqrt(user1.values().stream().mapToDouble(value -> Math.pow(value, 2)).sum());
        double sqrt2 = Math.sqrt(user2.values().stream().mapToDouble(value -> Math.pow(value, 2)).sum());
        double denominator = Math.fma(sqrt1, sqrt2, 0.0);
        if(denominator == 0){
            return 0;
        }
        double similarity = dotProduct / denominator;

// 修正到 [-1.0, 1.0] 范围内
        similarity = Math.max(-1.0, Math.min(1.0, similarity));

        return Double.isFinite(similarity) ? similarity : 0.0;
    }
}
