package com.guanshiyun;

import com.guanshiyun.rpc.qqCode.QQCode;
import com.guanshiyun.service.QQEmailVerificationCode.QQEmailCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QQCodeTest {
    @Autowired
    QQEmailCodeService qqEmailCodeService;

    @Test
    public void testSendCode() {
        Boolean block = qqEmailCodeService.sendQQEmailCode(new QQCode("1431809685@qq.com", "123456", 5))
                .block();
        System.out.println("=======================");
        System.out.println("发送成功 : " + block);
    }
}
