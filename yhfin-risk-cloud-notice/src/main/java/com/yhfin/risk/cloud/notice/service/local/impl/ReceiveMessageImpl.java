/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:49
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ReceiveMessageImpl.java
 * 文件描述: @Description 接收处理消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local.impl;


import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.notice.service.local.IReceiveMessage;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 接收处理消息
 * 包名称：com.yhfin.risk.cloud.notice.service.local.impl
 * 类名称：ReceiveMessageImpl
 * 类描述：接收处理消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/15:49
 */
@Service
@Slf4j
public class ReceiveMessageImpl implements IReceiveMessage {

    @Autowired
    private IStaticCalculateManageService manageService;
    /**
     * 接收分析结果消息
     *
     * @param message 消息体
     * @Title messageResult
     * @Description: 接收分析结果消息
     * @author: caohui
     * @Date: 2018/5/13/23:16
     */
    @StreamListener("analy")
    @Override
    public void messageAnaly(SingleFundAnalyResultDTO message) {
        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",收到消息服务器，分析基金结果消息,{}",  JSON.toJSONString(message));
        }
        CompletableFuture.runAsync(()->{
            manageService.hander(message);
        });
    }

    /**
     * 接收处理结果消息
     *
     * @param message 消息体
     * @Title messageResult
     * @Description: 接收处理结果消息
     * @author: caohui
     * @Date: 2018/5/13/23:16
     */
    @StreamListener("result")
    @Override
    public void messageResult(ResultHandleResultDTO message) {
        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",收到消息服务器，计算基金结果处理消息,{}", JSON.toJSONString(message));
        }
        CompletableFuture.runAsync(()->{
            manageService.hander(message);
        });
    }

}
