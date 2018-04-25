package com.yhfin.risk.result.message;

import com.yhfin.risk.common.requests.message.ResultMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 *
 * @author youlangta
 * @since 2018-04-17
 */
public interface IMessageService {

    /**
     * 发送基金结果处理消息
     *
     * @param message
     * @return
     */
    ServerResponse<ResultMessageSynchronizate> resultMessageSynchronizate(ResultMessageSynchronizate message);
}
