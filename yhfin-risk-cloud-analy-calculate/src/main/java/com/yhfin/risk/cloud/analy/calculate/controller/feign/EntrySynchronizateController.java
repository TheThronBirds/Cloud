/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/26/13:23
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: EntrySynchronizateController.java
 * 文件描述: @Description 接收条目同步请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.calculate.controller.feign;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.analy.calculate.service.local.IOverallManagerService;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 接收条目同步请求
 * 包名称：com.yhfin.risk.cloud.analy.calculate.controller.feign
 * 类名称：EntrySynchronizateController
 * 类描述：接收条目同步请求
 * 创建人：@author caohui
 * 创建时间：2018/5/26/13:23
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/analyCalculate")
public class EntrySynchronizateController {

    @Autowired
    private IOverallManagerService overallManagerService;

    /**
     * 条目同步消息
     * @Title entrySynchronizate
     * @Description: 条目同步消息
     * @param  entryMessage 条目同步消息体信息
     * @return
     * @author: caohui
     * @Date:  2018/5/26/15:19
     */
    @RequestMapping(value = "/entrySynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse entrySynchronizate(@RequestBody EntryMessageSynchronizateDTO entryMessage) {
        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(entryMessage.getSerialNumber(), entryMessage.getRequestId())
                    + ",接收到同步条目消息,{}", JSON.toJSONString(entryMessage));
        }
        CompletableFuture.runAsync(() -> {
            overallManagerService.handerEntryMessageSynchronizate(entryMessage);
        });
        return ServerResponse.createBySuccess(entryMessage.getRequestId(), entryMessage.getSerialNumber());
    }
}
