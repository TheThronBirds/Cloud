/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/17:25
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ReceiveMessageServiceImpl.java
 * 文件描述: @Description 接收消息中间键消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local.impl;

import com.yhfin.risk.cloud.analy.service.local.IReceiveMessageService;
import com.yhfin.risk.core.analy.manage.IEntryStaticAnalyManageService;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateNoticeDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.synchronizate.message.IMessageSynchronizateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 接收消息中间键消息
 * 包名称：com.yhfin.risk.cloud.analy.service.local.impl
 * 类名称：ReceiveMessageServiceImpl
 * 类描述：接收消息中间键消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/17:25
 */
@Service
@Slf4j
public class ReceiveMessageServiceImpl implements IReceiveMessageService {

    @Autowired
    private IMessageSynchronizateService messageSynchronizateService;

    @Autowired
    private IEntryStaticAnalyManageService entryStaticAnalyManageService;

    /**
     * 接收消息同步内存
     *
     * @param message 内存同步消息
     * @Title memorySynchronizateByMessage
     * @Description: 接收消息同步内存
     * @author: caohui
     * @Date: 2018/5/11/15:44
     */
    @StreamListener("memory")
    @Override
    public void memorySynchronizateByMessage(MemoryMessageSynchronizateDTO message) {
        if(log.isInfoEnabled()){
            log.info("收到同步内存消息");
        }
        CompletableFuture.runAsync(() -> {
            messageSynchronizateService.memorySynchronizateByMessage(message);
        });
    }

    /**
     * 接收消息同步条目
     *
     * @param message 条目同步消息
     * @Title entrySynchronizateByMessage
     * @Description: 接收消息同步条目
     * @author: caohui
     * @Date: 2018/5/11/15:44
     */
    @StreamListener("entry")
    @Override
    public void entrySynchronizateByMessage(EntryMessageSynchronizateDTO message) {
        if(log.isInfoEnabled()){
            log.info("收到同步条目消息");
        }
        CompletableFuture.runAsync(() -> {
            messageSynchronizateService.entrySynchronizateByMessage(message);
        });
    }


    /**
     * 同步计算结果
     * @Title updateStaticCalculateState
     * @Description: 同步计算结果
     * @param  staticCalculateNotice 消息
     * @author: caohui
     * @Date:  2018/5/14/2:42
     */
    @StreamListener("notice")
    @Override
    public void updateStaticCalculateState(StaticCalculateNoticeDTO staticCalculateNotice){
        if(log.isInfoEnabled()){
            log.info("收到同步静态计算总体结果信息");
        }
        CompletableFuture.runAsync(() -> {
            entryStaticAnalyManageService.updateStaticCalculateState(staticCalculateNotice);
        });
    }
}
