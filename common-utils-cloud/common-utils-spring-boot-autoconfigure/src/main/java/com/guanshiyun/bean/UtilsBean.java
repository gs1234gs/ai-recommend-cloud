package com.guanshiyun.bean;


import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.utils.WebContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsBean {

    @Bean
    public MyLong myLong() {
        return new MyLong();
    }

    @Bean
    public StringBuilder stringBuilder() {
        return new StringBuilder();
    }

    @Bean
    public StringBuffer stringBuffer() {
        return new StringBuffer();
    }
    @Bean
    public SnowflakePermanent snowflakePermanent() {
        // epoch 用固定时间，保证可读性和有序性
        long epoch = System.currentTimeMillis();
        int datacenterId = 1; // 数据中心ID
        int workerId = 1;     // 机器ID
        return new SnowflakePermanent(epoch, datacenterId, workerId);
    }
    @Bean
    public WebContextUtils webContextUtils() {
        return new WebContextUtils(myLong());
    }
}
