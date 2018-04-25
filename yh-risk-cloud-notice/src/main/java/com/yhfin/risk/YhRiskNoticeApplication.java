package com.yhfin.risk;

import com.yhfin.risk.notice.channel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringCloudApplication
@EnableBinding(value = InputChannels.class)
public class YhRiskNoticeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(YhRiskNoticeApplication.class, args);
	}
	
}
