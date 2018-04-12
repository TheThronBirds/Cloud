package com.yhfin.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.AbstractBaseMessageRequest;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.service.IMessageService;
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
     * 条目同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "messageSynchronizateFallBack")
    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizate(EntryMessageSynchronizate messageSynchronizate) {
        if (logger.isInfoEnabled()) {
            logger.info("接收到同步条目消息请求,{}", JSON.toJSONString(messageSynchronizate));
        }
        return restTemplate.postForObject("http://RISK-BUS/yhfin/bus/entryMessageSynchronizate", messageSynchronizate, ServerResponse.class);
    }

    /**
     * 内存同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "messageSynchronizateFallBack")
    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizate(MemoryMessageSynchronizate messageSynchronizate) {
        if (logger.isInfoEnabled()) {
            logger.info("接收到同步内存消息请求,{}", JSON.toJSONString(messageSynchronizate));
        }
        return restTemplate.postForObject("http://RISK-BUS/yhfin/bus/memoryMessageSynchronizate", messageSynchronizate, ServerResponse.class);

    }

    public ServerResponse messageSynchronizateFallBack(AbstractBaseMessageRequest message, Throwable e) {
        return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.exceptionErrorCode.NOTICE_ERROR_CODE, e.getMessage(), message);
    }
}
