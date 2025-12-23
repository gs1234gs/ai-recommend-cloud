package com.guanshiyun.rpc.gorseg;

import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.items.Item;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GorseProductServiceApi {

    //上传商品到gorse
    Mono<RowAffected> saveItem(List<Item> item);
    //上传商品到gorse
    Mono<RowAffected> saveItem(Item item);
    //保存行为·
    Mono<RowAffected> saveFeedback(List<Feedback> feedback);
    //保存用户·
    Mono<RowAffected> saveUser(List<User> user);
    //保存用户·
    Mono<RowAffected> saveUser(User user);
}
