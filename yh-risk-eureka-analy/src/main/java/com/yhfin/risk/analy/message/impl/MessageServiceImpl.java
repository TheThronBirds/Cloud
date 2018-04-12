package com.yhfin.risk.analy.message.impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.analy.message.IMessageService;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.synchronizate.entry.IEntrySynchronizateService;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMemorySynchronizateService memorySynchronizateService;

    @Autowired
    private IEntrySynchronizateService entrySynchronizateService;

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
        memorySynchronizateService.memorySynchronizateByMessage(message);
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
        entrySynchronizateService.entrySynchronizateByMessage(message);
    }
}
