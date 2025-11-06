package com.guanshiyun.dd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitListenerTest {
    @RabbitListener(queues = "test.queue")
    public void receive(String message) {
        System.out.println("======================================================");
        System.out.println("receive:" + message);
        System.out.println("======================================================");
    }
    @RabbitListener(queues = "test.queue")
    public void receive2(String message) {
        System.err.println("======================================================");
        System.err.println("receive2:" + message);
        System.err.println("======================================================");

    }
}
