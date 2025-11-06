package com.guanshiyun;

import com.guanshiyun.print.start.SystemStartPrintln;
import com.guanshiyun.utils.DisableBusinessWebFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DisableBusinessWebFilter
public class GatewayServerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerAppApplication.class, args);
		SystemStartPrintln.myPrintln();
	}
}
