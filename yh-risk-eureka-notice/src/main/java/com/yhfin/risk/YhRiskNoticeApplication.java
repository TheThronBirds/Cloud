package com.yhfin.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;



@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class YhRiskNoticeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(YhRiskNoticeApplication.class, args);
	}
	
}
