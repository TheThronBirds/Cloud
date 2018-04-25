package com.yhfin.risk;

import com.yhfin.risk.analy.chanel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringCloudApplication
@EnableBinding(InputChannels.class)
public class YhRiskAnalyApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(YhRiskAnalyApplication.class, args);
	}
	
}
