package com.guanshiyun;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainTest {

    @Autowired
   private DashScopeAgentApi dashScopeAgentApi;
    @Test
    void contextLoads() {
        // 只要能执行到这行，就说明注入成功了
        System.out.println("注入成功！dashScopeAgentApi = " + dashScopeAgentApi);
        // 也可以打印类名，确认具体实现
        System.out.println("实际类型: " + dashScopeAgentApi.getClass().getName());
    }


}
