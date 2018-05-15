/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/20:53
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendMessageController.java
 * 文件描述: @Description 接收消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.controller.feign;

import com.yhfin.risk.cloud.notice.service.feign.ISendMessageService;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.sql.build.IEntryBuildSqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接收消息
 * 包名称：com.yhfin.risk.cloud.notice.controller.feign
 * 类名称：SendMessageController
 * 类描述：接收消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/20:53
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/notice")
public class SendMessageController {

    @Autowired
    private ISendMessageService messageService;

    @Autowired
    private IEntryBuildSqlService buildSqlService;

    /**
     * 发送条目同步消息
     *
     * @param message 消息体
     * @return 接口返回信息
     * @Title entryMessageSynchronizate
     * @Description: 发送条目同步消息
     * @author: caohui
     * @Date: 2018/5/13/15:37
     */
    @RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    ServerResponse entryMessageSynchronizate(@RequestBody EntryMessageSynchronizateDTO message) {
        if (log.isInfoEnabled()) {
            log.info("接收同步条目消息请求");
        }
        if (message.getSynchronizateAll() != null && message.getSynchronizateAll()) {
            buildSqlService.entryBuildSqlAll();
        } else {
            List<String> updateRiskIds = message.getUpdateRiskIds();
            if (updateRiskIds != null && !updateRiskIds.isEmpty()) {
                buildSqlService.entryBuildSqls(updateRiskIds.toArray(new String[updateRiskIds.size()]));
            }
        }
        return messageService.entryMessageSynchronizate(message);
    }

    /**
     * 发送内存同步消息
     *
     * @param message 消息体
     * @return 接口返回信息
     * @Title memoryMessageSynchronizate
     * @Description: 发送内存同步消息
     * @author: caohui
     * @Date: 2018/5/13/15:37
     */
    @RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    ServerResponse memoryMessageSynchronizate(@RequestBody MemoryMessageSynchronizateDTO message) {
        if (log.isInfoEnabled()) {
            log.info("接收同步内存消息请求");
        }
        return messageService.memoryMessageSynchronizate(message);
    }

}
