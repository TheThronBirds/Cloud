/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:40
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IMessageService.java
 * 文件描述: @Description 处理消息服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2017
 *
 ********************************************************/
package com.yhfin.risk.cloud.bus.service.message;

import com.yhfin.risk.core.common.types.ChannelTypeEnum;

/**
 * 处理消息服务
 * 包名称：com.yhfin.risk.cloud.bus.service.message
 * 类名称：IMessageService
 * 类描述：处理消息服务
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:40
 */

public interface IMessageService {

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
    boolean sendMessage(Object message, ChannelTypeEnum channelType);
}
