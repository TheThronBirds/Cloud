/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:33
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ISendMessageService.java
 * 文件描述: @Description 发送消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.feign;

import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 发送消息
 * 包名称：com.yhfin.risk.cloud.notice.service.feign
 * 类名称：ISendMessageService
 * 类描述：发送消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/15:33
 */
@FeignClient(value = "risk-bus", fallbackFactory = SendMessageServiceHystrix.class)
@RequestMapping("/yhfin/cloud/bus")
public interface ISendMessageService {
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
    ServerResponse entryMessageSynchronizate(@RequestBody EntryMessageSynchronizateDTO message);

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
    ServerResponse memoryMessageSynchronizate(@RequestBody MemoryMessageSynchronizateDTO message);


}
