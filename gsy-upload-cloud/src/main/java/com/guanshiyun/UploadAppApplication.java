package com.guanshiyun;

import com.guanshiyun.audit.AuditingConfig;
import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = {
        R2dbcAutoConfiguration.class,
        AuditingConfig.class
})
public class UploadAppApplication
{
    public static void main( String[] args )
    {

        SpringApplication.run(UploadAppApplication.class, args);
        SystemStartPrintln.myPrintln();
    }
}
