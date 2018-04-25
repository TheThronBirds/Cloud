package com.yhfin.risk.notice.service;

import com.yhfin.risk.common.pojos.notice.StaticSingleFundCalculateResult;
import com.yhfin.risk.common.requests.calculate.StaticSingleFundCalculateRequest;
import com.yhfin.risk.common.requests.message.AnalyMessageSynchronizate;
import com.yhfin.risk.common.requests.message.CalculateMessageSynchronizate;
import com.yhfin.risk.common.requests.message.ResultMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 * 发送基金计算服务
 * @author youlangta
 * @since  2018-04-10
 */
public interface ICalculateService {
    /**
     * 单个基金静态计算请求
     * @param calculateRequest
     * @return
     */
    ServerResponse<StaticSingleFundCalculateResult>  sendSingleStaticCalculate(StaticSingleFundCalculateRequest calculateRequest);

    void handleAnalyMessage(AnalyMessageSynchronizate message);

    void handleCalculateMessage(CalculateMessageSynchronizate message);

    void handleResultMessage(ResultMessageSynchronizate message);
}
