package com.guanshiyun;

import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class BehaviorAppApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(BehaviorAppApplication.class, args);
        SystemStartPrintln.myPrintln();
    }
}
