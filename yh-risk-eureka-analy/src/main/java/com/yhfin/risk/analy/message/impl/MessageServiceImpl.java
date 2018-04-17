package com.yhfin.risk.analy.message.impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.analy.message.IMessageService;
import com.yhfin.risk.common.requests.message.AnalyMessageSynchronizate;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.synchronizate.entry.IEntrySynchronizateService;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;
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
    private IEntrySynchronizateService entrySynchronizateService;


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
     * 同步条目消息
     *
     * @param message
     */
    @StreamListener("entry")
    @Override
    public void messageEntry(EntryMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "收到消息服务器，同步条目消息", message.getSerialNumber(), message.getRequestId());
        }
        if (logger.isDebugEnabled()) {
            logger.debug(StringUtil.commonLogStart() + "收到消息服务器，同步条目消息,消息:{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }
        CompletableFuture.runAsync(() -> {
            entrySynchronizateService.entrySynchronizateByMessage(message);
        });

    }

    /**
     * 内存分析消息
     *
     * @param messageSynchronizate
     * @return
     */
    @Override
    public ServerResponse<AnalyMessageSynchronizate> analyMessageSynchronizate(AnalyMessageSynchronizate messageSynchronizate) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发送基金分析结果消息,{}", messageSynchronizate.getSerialNumber(), messageSynchronizate.getRequestId(), JSON.toJSONString(messageSynchronizate));
        }
        return restTemplate.postForObject("http://RISK-BUS/yhfin/bus/analyMessageSynchronizate", messageSynchronizate, ServerResponse.class);
    }
}
