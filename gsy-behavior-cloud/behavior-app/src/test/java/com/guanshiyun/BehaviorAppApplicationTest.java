package com.guanshiyun;

import com.guanshiyun.service.collect.UserCollectService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import io.gorse.gorse4j.Gorse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.util.context.Context;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class BehaviorAppApplicationTest
{
    @Autowired
    private UserCollectService userCollectService;

    @Test
    public void test()
    {
        userCollectService.findAll(10)
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(17)))
                .collectList()
                .map(i->{
                    System.out.println("=======================");
                    i.forEach(System.out::println);
                    System.out.println("=======================");
                    return i;
                })
                .doOnSuccess(i->{
                    System.out.println("=======================");
                    i.forEach(System.out::println);
                    System.out.println("=======================");
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }


    public static void main(String[] args) throws IOException {
        // Create a client.
        Gorse client = new Gorse("http://127.0.0.1:8087", "api_key");

        // Insert a user.

        // Get recommendation.
        List<String> recommend = client.getRecommend("17");
        System.out.println(recommend);
    }

}
