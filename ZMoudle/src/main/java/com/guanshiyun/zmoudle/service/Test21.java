package com.guanshiyun.zmoudle.service;

import io.gorse.gorse4j.Feedback;
import io.gorse.gorse4j.Gorse;
import io.gorse.gorse4j.Item;

import java.io.IOException;
import java.util.List;

public class Test21 {
    public static void main(String[] args) throws IOException {
        // Create a client.
        Item item = new Item("100", false, List.of("book"), List.of("book"), "2022-11-20T13:55:27Z", "");
        Item item1 = new Item("300", false, List.of("book"), List.of("book"), "2022-11-20T13:55:27Z", "");

        Gorse client = new Gorse("http://127.0.0.1:8087", "api_key");
        client.insertItem(item);
        client.insertItem(item1);

        // Insert feedback.
        List<Feedback> feedbacks = List.of(
                new Feedback("read", "100", "300", "2022-11-20T13:55:27Z"),
                new Feedback("read", "100", "400", "2022-11-20T13:55:27Z")
        );
        client.insertFeedback(feedbacks);

        // Get recommendation.
        List<String> recommend = client.getRecommend("100");
        System.out.println(recommend);
    }

}
