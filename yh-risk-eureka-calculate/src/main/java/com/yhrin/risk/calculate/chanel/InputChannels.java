package com.yhrin.risk.calculate.chanel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 接收消息通道
 * 
 * @author youlangta
 * @since 2018-01-29
 */
public interface InputChannels {

	/**
	 * 内存从风控同步
	 * 
	 * @return
	 */
	@Input("memory")
	SubscribableChannel memory();


}
