package com.yhfin.risk.result.message.Impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.requests.message.ResultMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.result.message.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class MessageServiceImpl implements IMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送基金结果处理消息
     *
     * @param message
     * @return
     */
    @Override
    public ServerResponse<ResultMessageSynchronizate> resultMessageSynchronizate(ResultMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发送计算结果处理消息,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }
        ServerResponse serverResponse = restTemplate.postForObject("http://RISK-BUS/yhfin/bus/resultMessageSynchronizate", message, ServerResponse.class);
        if (!serverResponse.isSuccess()) {
            if (logger.isErrorEnabled()) {
                logger.error(StringUtil.commonLogStart() + "发送计算结果处理消息失败,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
                logger.error(serverResponse.getReturnMsg());
            }
        }
        return serverResponse;
    }
}
