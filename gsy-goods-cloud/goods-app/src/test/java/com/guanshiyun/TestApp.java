package com.guanshiyun;

import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.user.User;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
public class TestApp {
    @Autowired
    private GorseClient gorseClient;

    @Test
    public void test1()
    {

        List<String> block = gorseClient.getRecommend("17")
                .block();
        System.out.println("gorse ： "+ block);
        User user = gorseClient.findUser("17").block();
        System.out.println("user : "+ user);
        List<Feedback> click = gorseClient.listFeedback("17", GorseFeedbackEnum.BROWSE.getValue()).block();
        System.out.println("BROWSE : "+ click);

    }



}


