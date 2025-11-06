package com.guanshiyun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitListenerTest {
    @RabbitListener
    public void receive(String message) {
        System.out.println("======================================================");
        System.out.println("receive:" + message);
        System.out.println("======================================================");
    }
}
