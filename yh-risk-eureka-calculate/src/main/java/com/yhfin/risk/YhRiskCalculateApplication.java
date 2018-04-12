package com.yhfin.risk;

import com.yhfin.risk.calculate.chanel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringCloudApplication
@EnableBinding(InputChannels.class)
public class YhRiskCalculateApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(YhRiskCalculateApplication.class, args);
	}
	
}
