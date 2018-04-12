package com.yhfin.risk.bus.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息通道
 * 
 * @author youlangta
 * @since 2018-03-20
 * 
 */
public interface OutputChannels {

	/**
	 * 发布同步内存数据消息
	 * 
	 * @return
	 */
	@Output("memory")
	MessageChannel memory();

	/**
	 * 发布同步条目数据消息
	 * 
	 * @return
	 */
	@Output("entry")
	MessageChannel entry();

	/**
	 * 发布分析信息
	 * 
	 * @return
	 */
	@Output("analy")
	MessageChannel analy();

	/**
	 * 发布分析信息
	 * 
	 * @return
	 */
	@Output("risk")
	MessageChannel risk();

	/**
	 * 发布计算信息
	 * 
	 * @return
	 */
	@Output("calculate")
	MessageChannel calculate();

	/**
	 * 发布查询计算进度信息
	 * 
	 * @return
	 */
	@Output("queryCalculate")
	MessageChannel queryCalculate();

}
