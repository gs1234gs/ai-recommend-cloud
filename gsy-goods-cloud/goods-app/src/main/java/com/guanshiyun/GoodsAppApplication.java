package com.guanshiyun;


import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoodsAppApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(GoodsAppApplication.class, args);
        SystemStartPrintln.myPrintln();
    }
}
