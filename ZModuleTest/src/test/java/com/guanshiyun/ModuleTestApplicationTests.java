package com.guanshiyun;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ModuleTestApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

}
