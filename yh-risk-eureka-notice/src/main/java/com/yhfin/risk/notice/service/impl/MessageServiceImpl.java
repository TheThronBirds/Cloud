package com.yhfin.risk.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.*;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.SerializeUtil;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IJedisClusterDao;
import com.yhfin.risk.notice.service.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

@Service
public class MessageServiceImpl implements IMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 内存同步在redis中存储的消息，hashkey值
     */
    private final byte[] MEMORY = "MEMORY".getBytes();
    /**
     * 条目同步在redis中存储的消息，hashkey值
     */
    private final byte[] ENTRY = "ENTRY".getBytes();


    @Autowired
    private IJedisClusterDao jedisClusterDao;


    @Autowired
    private RestTemplate restTemplate;

    /**
     * 条目同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "entryMessageSynchronizateFallBack")
    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizate(EntryMessageSynchronizate messageSynchronizate) {
        if (logger.isInfoEnabled()) {
            logger.info("发送同步条目消息请求,{}", JSON.toJSONString(messageSynchronizate));
        }
        byte[] versionBytes = jedisClusterDao.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, ENTRY);
        Integer versionNumber = null;
        if(versionBytes == null){
            versionNumber = 0;
        }else{
             versionNumber = Integer.valueOf(new String(versionBytes)) + 1;
        }
        messageSynchronizate.setEntryVersionNumber(versionNumber);
        ServerResponse serverResponse =  restTemplate.postForObject("http://RISK-BUS/yhfin/bus/entryMessageSynchronizate", messageSynchronizate, ServerResponse.class);
        if(serverResponse.isSuccess()) {
            jedisClusterDao.hset(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, ENTRY, String.valueOf(versionNumber).getBytes());
            jedisClusterDao.hset(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_ENTRY, String.valueOf(versionNumber).getBytes(), SerializeUtil.serialize(messageSynchronizate));
        }
        return serverResponse;
    }

    /**
     * 内存同步消息
     *
     * @param messageSynchronizate
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "memoryMessageSynchronizateFallBack")
    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizate(MemoryMessageSynchronizate messageSynchronizate) {
        if (logger.isInfoEnabled()) {
            logger.info("发送同步内存消息请求,{}", JSON.toJSONString(messageSynchronizate));
        }
        byte[] versionBytes = jedisClusterDao.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, MEMORY);
        Integer versionNumber = null;
        if(versionBytes == null){
            versionNumber = 0;
        }else{
            versionNumber = Integer.valueOf(new String(versionBytes)) + 1;
        }
        messageSynchronizate.setMemoryVersionNumber(versionNumber);
        ServerResponse serverResponse = restTemplate.postForObject("http://RISK-BUS/yhfin/bus/memoryMessageSynchronizate", messageSynchronizate, ServerResponse.class);
        if(serverResponse.isSuccess()){
            jedisClusterDao.hset(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, MEMORY, String.valueOf(versionNumber).getBytes());
            jedisClusterDao.hset(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_MEMORY, String.valueOf(versionNumber).getBytes(), SerializeUtil.serialize(messageSynchronizate));
        }
        return  serverResponse;
    }

    /**
     * 基金分析消息
     *
     * @param message
     */
    @StreamListener("entry")
    @Override
    public void messageAnaly(AnalyMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "收到消息服务器，分析基金结果消息,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }

    }

    /**
     * 基金分析消息
     *
     * @param message
     */
    @StreamListener("calculate")
    @Override
    public void messageCalculate(CalculateMessageSynchronizate message) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "收到消息服务器，计算基金结果消息,{}", message.getSerialNumber(), message.getRequestId(), JSON.toJSONString(message));
        }


    }


    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizateFallBack(MemoryMessageSynchronizate message, Throwable e) {
        return messageSynchronizateFallBack(message, e);
    }

    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizateFallBack(EntryMessageSynchronizate message, Throwable e) {
        return messageSynchronizateFallBack(message, e);
    }

    private ServerResponse messageSynchronizateFallBack(AbstractBaseMessageRequest message, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "消息发送失败，{}", message.getSerialNumber(), message.getRequestId(), e.getMessage());
            logger.error(""+e,e);
        }
        return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.exceptionErrorCode.NOTICE_ERROR_CODE, e.getMessage(), message);

    }


}
