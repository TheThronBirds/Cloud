/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:40
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: MessageServiceImpl.java
 * 文件描述: @Description 处理消息服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.bus.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.bus.service.message.IMessageService;
import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.AbstractMessageDTO;
import com.yhfin.risk.core.common.pojos.dtos.MessageResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.types.ChannelTypeEnum;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 处理消息服务
 * 包名称：com.yhfin.risk.cloud.bus.service.message.impl
 * 类名称：MessageServiceImpl
 * 类描述：处理消息服务
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:40
 */
@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {


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
    @Qualifier("result")
    private MessageChannel resultChannel;


    /**
     * 发送消息
     *
     * @param message 消息体
     * @return 是否成功
     * @Title sendMessage
     * @Description: 发送消息
     * @author: caohui
     * @Date: 2018/5/13/14:47
     */
    @Override
    public boolean sendMessage(AbstractMessageDTO message) {

        if (message.channelType() != null) {
            if (log.isInfoEnabled()) {
                log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",发送{}消息,{}",
                        message.channelType().getTypeDes(), JSON.toJSONString(message));
            }
        }
        switch (message.channelType()) {
            case MEMORY:
                return memoryChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case ENTRY:
                return entryChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case ANALY:
                return analyChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            case RESULT:
                return resultChannel.send(MessageBuilder.withPayload(message).build(), 1000 * 60 * 5);
            default:
                if (log.isErrorEnabled()) {
                    log.error("没有对应的消息类型,消息{},消息类型{}", JSON.toJSONString(message), JSON.toJSONString(message.channelType()));
                }
                return false;
        }

    }


}
