package com.guanshiyun;

import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class RecommendAppApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(RecommendAppApplication.class, args);
        SystemStartPrintln.myPrintln();
    }
}
