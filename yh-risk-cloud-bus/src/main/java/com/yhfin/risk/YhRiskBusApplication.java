package com.yhfin.risk;

import com.yhfin.risk.bus.channel.OutputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringCloudApplication
@EnableBinding(OutputChannels.class)
public class YhRiskBusApplication {

	public static void main(String[] args) {
		SpringApplication.run(YhRiskBusApplication.class, args);
	}
}
