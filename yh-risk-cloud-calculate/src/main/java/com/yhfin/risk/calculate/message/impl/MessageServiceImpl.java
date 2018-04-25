package com.yhfin.risk.calculate.message.impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.requests.message.CalculateMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;
import com.yhfin.risk.calculate.message.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageServiceImpl implements IMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMemorySynchronizateService memorySynchronizateService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 同步内存消息
     *
     * @param message
     */
    @StreamListener("memory")
    @Override
    public void messageMeory(MemoryMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "收到消息服务器，同步内存消息", message.getSerialNumber(), message.getRequestId());
        }
        if (logger.isDebugEnabled()) {
            logger.debug(StringUtil.commonLogStart() + "收到消息服务器，同步内存消息,消息:{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }
        CompletableFuture.runAsync(() -> {
            memorySynchronizateService.memorySynchronizateByMessage(message);
        });
    }

    /**
     * 内存计算消息
     *
     * @param message
     * @return
     */
    @Override
    public ServerResponse<CalculateMessageSynchronizate> calculateMessageSynchronizate(CalculateMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发送计算结果消息,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }
        ServerResponse serverResponse = restTemplate.postForObject("http://RISK-BUS/yhfin/bus/calculateMessageSynchronizate", message, ServerResponse.class);
        if (!serverResponse.isSuccess()) {
            if (logger.isErrorEnabled()) {
                logger.error(StringUtil.commonLogStart() + "发送计算结果消息失败,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
                logger.error(serverResponse.getReturnMsg());
            }
        }
        return serverResponse;
    }
}
