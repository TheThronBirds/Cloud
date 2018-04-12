package com.yhfin.risk.bus.service;

import com.yhfin.risk.common.types.ChannelType;

/**
 * 发送消息
 * @author youlangta
 * @since 2018-03-20
 */
public interface ISendMessageService {
	/**
	 * 发送消息
	 * @param message
	 * @return
	 */
	boolean sendMessage(Object message, ChannelType channelType);
}
