package com.yhfin.risk.analy.message;

import com.yhfin.risk.common.requests.message.AnalyMessageSynchronizate;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 * 接收消息，处理消息
 * 
 * @author youlangta
 * @since 2018-03-20
 */
public interface IMessageService {

	/**
	 * 同步内存消息
	 * 
	 * @param message
	 */
	void messageMeory(MemoryMessageSynchronizate message);

	/**
	 * 同步条目消息
	 * 
	 * @param message
	 */
	void messageEntry(EntryMessageSynchronizate message);


	/**
	 * 内存分析消息
	 *
	 * @param messageSynchronizate
	 * @return
	 */
	ServerResponse<AnalyMessageSynchronizate> analyMessageSynchronizate(AnalyMessageSynchronizate messageSynchronizate);
}
