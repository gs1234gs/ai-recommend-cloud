package com.guanshiyun;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class GatewayServerAppApplicationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    public void contextLoads() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
