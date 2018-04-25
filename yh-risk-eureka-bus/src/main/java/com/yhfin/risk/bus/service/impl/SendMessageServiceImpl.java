package com.yhfin.risk.bus.service.impl;

import com.yhfin.risk.bus.service.ISendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.types.ChannelType;

@Service
public class SendMessageServiceImpl implements ISendMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("risk")
    private MessageChannel riskChannel;

    @Autowired
    @Qualifier("memory")
    private MessageChannel memoryChannel;

    @Autowired
    @Qualifier("entry")
    private MessageChannel entryChannel;

    @Autowired
    @Qualifier("analy")
    private MessageChannel analyChannel;

    @Autowired
    @Qualifier("calculate")
    private MessageChannel calculateChannel;

    @Autowired
    @Qualifier("result")
    private MessageChannel resultChannel;

    @Override
    public boolean sendMessage(Object message, ChannelType channelType) {
        if (logger.isDebugEnabled()) {
            logger.debug("收到消息{},消息类型{}", message, channelType);
        }
        switch (channelType) {
            case MEMORY:
                return memoryChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case ENTRY:
                return entryChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case ANALY:
                return analyChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case CALCULATE:
                return calculateChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case RISK:
                return riskChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case RESULT:
                return resultChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error("没有对应的消息类型,消息{},消息类型{}", JSON.toJSONString(message), JSON.toJSONString(channelType));
                }
                return false;
        }
    }

}
