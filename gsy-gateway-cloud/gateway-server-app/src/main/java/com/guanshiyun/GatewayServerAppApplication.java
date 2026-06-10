package com.guanshiyun;

import com.guanshiyun.audit.AuditingConfig;
import com.guanshiyun.print.start.SystemStartPrintln;
import com.guanshiyun.utils.DisableBusinessWebFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(
		exclude = {
				R2dbcAutoConfiguration.class,
				AuditingConfig.class
		}
)
@DisableBusinessWebFilter
public class GatewayServerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerAppApplication.class, args);
		SystemStartPrintln.myPrintln();
	}
}
