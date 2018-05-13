/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/17:24
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IReceiveMessageService.java
 * 文件描述: @Description 接收消息中间键消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2017
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local;

import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;

/**
 * 接收消息中间键消息
 * 包名称：com.yhfin.risk.cloud.analy.service.local
 * 类名称：IReceiveMessageService
 * 类描述：接收消息中间键消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/17:24
 */

public interface IReceiveMessageService {
    /**
     * 接收消息同步内存
     *
     * @param message 内存同步消息
     * @Title memorySynchronizateByMessage
     * @Description: 接收消息同步内存
     * @author: caohui
     * @Date: 2018/5/11/15:44
     */
    void memorySynchronizateByMessage(MemoryMessageSynchronizateDTO message);


    /**
     * 接收消息同步条目
     *
     * @param message 条目同步消息
     * @Title entrySynchronizateByMessage
     * @Description: 接收消息同步条目
     * @author: caohui
     * @Date: 2018/5/11/15:44
     */
    void entrySynchronizateByMessage(EntryMessageSynchronizateDTO message);
}
