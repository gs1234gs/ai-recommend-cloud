//package com.xinghe.zmoudle;
//
//import com.guanshiyun.dd.dubbTest.DubboTest;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestDuboController implements CommandLineRunner {
//
//    @DubboReference
//    private DubboTest dubboTest;
//
//    @Override
//    public void run(String... args) throws Exception {
//       dubboTest.sayHello("xinghe")
//               .doOnSuccess(System.out::println)
//               .block();
//    }
//}
