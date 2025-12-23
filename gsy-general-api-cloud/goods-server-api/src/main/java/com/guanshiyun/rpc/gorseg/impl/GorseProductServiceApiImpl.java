package com.guanshiyun.rpc.gorseg.impl;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.items.Item;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.gorseg.GorseProductServiceApi;
import com.guanshiyun.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GorseProductServiceApiImpl implements GorseProductServiceApi {
    private final GorseClient gorseClient;

    @Override
    public Mono<RowAffected> saveItem(List<Item> item) {
        return Flux.fromIterable(item)
                .flatMap(gorseClient::saveItem)
                .reduce(ConstNumber.INT_ZERO,(total,result)->result.getRowAffected())
                .map(total->RowAffected.builder().rowAffected(total).build());
    }

    @Override
    public Mono<RowAffected> saveItem(Item item) {
        return gorseClient.saveItem(item);
    }

    @Override
    public Mono<RowAffected> saveFeedback(List<Feedback> feedback) {
        return gorseClient.insertFeedback( feedback);
    }

    @Override
    public Mono<RowAffected> saveUser(List<User> user) {
        return Flux.fromIterable(user)
                .flatMap(gorseClient::saveUser)
                .reduce(ConstNumber.INT_ZERO,(total,result)->result.getRowAffected())
                .map(total->RowAffected.builder().rowAffected(total).build());
    }

    @Override
    public Mono<RowAffected> saveUser(User user) {
        return gorseClient.saveUser(user);
    }
}
