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


    private ExecutorService handerMessagePool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(20000),
            (runnable) -> {
                return new Thread(runnable, "分析计算服务，处理同步条目、内存、单个基金计算请求线程池单线程");
            });

    private BlockingDeque<AbstractMessageDTO> messagePoolDeque;

    {
        this.messagePoolDeque = new LinkedBlockingDeque<>(1000000);
    }


    @PostConstruct
    public void init() {
        handerMessagePool.execute(() -> {
            while (true) {
                try {
                    AbstractMessageDTO message = messagePoolDeque.take();
                    if (message != null) {
                        if (message instanceof MemoryMessageSynchronizateDTO) {
                            sendMessage(message, ChannelTypeEnum.MEMORY);
                        } else if (message instanceof EntryMessageSynchronizateDTO) {
                            sendMessage(message, ChannelTypeEnum.ENTRY);
                        } else if (message instanceof SingleFundAnalyResultDTO) {
                            sendMessage(message, ChannelTypeEnum.ANALY);
                        } else if (message instanceof ResultHandleResultDTO) {
                            sendMessage(message, ChannelTypeEnum.RESULT);
                        }
                    }
                } catch (InterruptedException e) {
                    if (log.isErrorEnabled()) {
                        log.error("取出消息失败", e);
                    }
                }
            }
        });
    }

    /**
     * 发送消息
     *
     * @param message     消息体
     * @param channelType 消息类型
     * @return 是否成功
     * @Title sendMessage
     * @Description: 发送消息
     * @author: caohui
     * @Date: 2018/5/13/14:47
     */
    @Override
    public boolean sendMessage(Object message, ChannelTypeEnum channelType) {
        if (log.isDebugEnabled()) {
            log.debug("收到消息{},消息类型{}", message, channelType);
        }
        switch (channelType) {
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
                    log.error("没有对应的消息类型,消息{},消息类型{}", JSON.toJSONString(message), JSON.toJSONString(channelType));
                }
                return false;
        }
    }

    /**
     * 接收消息
     *
     * @param message     消息
     * @param channelType 消息类型
     * @Title putMessage
     * @Description: 接收消息
     * @author: caohui
     * @Date: 2018/5/26/18:15
     */
    @Override
    public void putMessage(AbstractMessageDTO message, ChannelTypeEnum channelType) {
        boolean valid = checkMessage(message, channelType);
        if (valid) {
            try {
                messagePoolDeque.put(message);
            } catch (InterruptedException e) {
                if (log.isErrorEnabled()) {
                    log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",把消息放入队列失败", e);
                }
            }
        }
    }


    /**
     * 校验消息
     *
     * @param message 消息体
     * @return 校验结果
     * @throws @Title checkMessage 校验消息
     * @Description: 校验消息
     * @author: caohui
     * @Date: 2018/5/13/15:07
     */
    private boolean checkMessage(AbstractMessageDTO message, ChannelTypeEnum channelType) {
        if (message == null) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart("", "") + ",发布消息体为空");
            }
            return false;
        }

        if (StringUtils.isBlank(message.getRequestId()) || StringUtils.isBlank(message.getSerialNumber())) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart("", "") + ",请求序号为空或者流水号为空");
            }
            return false;
        }

        if (message.channelType() == null) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",消息体类型为空");
            }
            return false;
        }

        if (message.channelType() != channelType) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",消息体类型接口错误,接口消息类型为" + message.getChannelType().getTypeDes()
                        + ",发布消息的消息类型为" + channelType.getTypeDes());
            }
            return false;
        }
        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",请求发布消息{},消息类型{}",
                    JSON.toJSONString(message), message.getChannelType().getTypeDes());
        }
        return true;
    }

}
