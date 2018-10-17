/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/26/13:24
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: MemorySynchronizateController.java
 * 文件描述: @Description 内存同步请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.calculate.controller.feign;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.analy.calculate.service.local.IOverallManagerService;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 内存同步请求
 * 包名称：com.yhfin.risk.cloud.analy.calculate.controller.feign
 * 类名称：MemorySynchronizateController
 * 类描述：内存同步请求
 * 创建人：@author caohui
 * 创建时间：2018/5/26/13:24
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/analyCalculate")
public class MemorySynchronizateController {
    @Autowired
    private IOverallManagerService overallManagerService;
    /**
     * 内存同步消息
     * @Title entrySynchronizate
     * @Description: 条目同步消息
     * @param  memoryMessage 内存同步消息
     * @return
     * @author: caohui
     * @Date:  2018/5/26/15:19
     */
    @RequestMapping(value = "/memorySynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse memorySynchronizate(@RequestBody MemoryMessageSynchronizateDTO memoryMessage) {

        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(memoryMessage.getSerialNumber(), memoryMessage.getRequestId())
                    + ",接收到同步内存消息,{}", JSON.toJSONString(memoryMessage));
        }
        CompletableFuture.runAsync(() -> {
            overallManagerService.handerMemoryMessageSynchronizate(memoryMessage);
        });
        return ServerResponse.createBySuccess(memoryMessage.getRequestId(), memoryMessage.getSerialNumber());
    }
    
    /**
     * 内存同步状态查询消息
     * @Title entrySynchronizate
     * @Description: 条目同步消息
     * @return
     * @author: benguolong 
     * @Date:  2018/5/26/15:19
     */
    @RequestMapping(value = "/memorySynchronizateStatus", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse memorySynchronizateStatus() {
        if (log.isInfoEnabled()) {
            log.info("接收到内存同步状态查询消息");
        }
        return ServerResponse.createBySuccess("", "",overallManagerService.getSynchronizateTableDataStatusDTO());
    }
}
