package com.guanshiyun;

import com.guanshiyun.rpc.behaviorapi.browse.UserBrowseServiceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import reactor.test.StepVerifier;

/**
 * Unit test for simple App.
 */
// 推荐：精准测试 Feign Client
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RecommendAppApplication.class) // 明确指定主类
public class RecommendAppApplicationTest {

    @Autowired
    private UserBrowseServiceApi userBrowseServiceApi;

    @Test
    public void test() {
        userBrowseServiceApi.findUserBrowseRecord(10).subscribe(System.out::println);
    }
    @Test
    public void test1() {
        System.out.println("Bean 类型: " + userBrowseServiceApi.getClass());
        userBrowseServiceApi.findUserBrowseRecord(10).subscribe(System.out::println);
    }

    @Test
    void testFeignProxy() {
        // 1. 验证是否为代理
        System.out.println("Bean 类型: " + userBrowseServiceApi.getClass());
        System.out.println("是否为代理: " + Proxy.isProxyClass(userBrowseServiceApi.getClass()));

        // 2. 验证 Feign 特性
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        System.out.println("当前线程是否有 RequestAttributes: " + (attributes != null));
    }

    @Test
    void testRemoteCall() {
        // 3. 调用远程接口
        StepVerifier.create(userBrowseServiceApi.findUserBrowseRecord(10))
                .expectNextCount(10) // 假设 behavior-service 返回 10 条
                .expectComplete()
                .verify();
    }
}
