package com.guanshiyun;

import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class ReviewAppApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ReviewAppApplication.class, args);
        SystemStartPrintln.myPrintln();
    }
}
