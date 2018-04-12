package com.yhfin.risk.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 接收消息通道
 * @author youlangta
 * @since 2018-01-29
 */
public interface InputChannels {
	
	@Input("analy")
	SubscribableChannel analy();
	
	@Input("calculate")
	SubscribableChannel calculate();
}
