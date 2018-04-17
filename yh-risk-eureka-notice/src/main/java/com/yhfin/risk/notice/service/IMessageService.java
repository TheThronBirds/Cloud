package com.yhfin.risk.notice.service;


import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.AnalyMessageSynchronizate;
import com.yhfin.risk.common.requests.message.CalculateMessageSynchronizate;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 * 向消息中间件发送消息服务
 *
 * @author youlangta
 * @since 2018-04-12
 */
public interface IMessageService {
    /**
     * 条目同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizate(EntryMessageSynchronizate messageSynchronizate);

    /**
     * 内存同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizate(MemoryMessageSynchronizate messageSynchronizate);

    /**
     * 基金分析消息
     *
     * @param message
     */
    void messageAnaly(AnalyMessageSynchronizate message);


    /**
     * 基金分析消息
     *
     * @param message
     */
    void messageCalculate(CalculateMessageSynchronizate message);
}
