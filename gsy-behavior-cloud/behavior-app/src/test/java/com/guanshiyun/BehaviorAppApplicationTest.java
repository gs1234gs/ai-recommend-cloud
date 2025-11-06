package com.guanshiyun;

import com.guanshiyun.service.browse.UserBrowseService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.util.context.Context;

import java.math.BigInteger;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class BehaviorAppApplicationTest
{
    @Autowired
    private UserBrowseService userBrowseService;

    @Test
    public void test()
    {
        userBrowseService.findAll(10)
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
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

}
