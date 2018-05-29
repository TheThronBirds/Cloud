/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:23
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: InputChannels.java
 * 文件描述: @Description 接收消息通道
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 接收消息通道
 * 包名称：com.yhfin.risk.cloud.notice.channel
 * 类名称：InputChannels
 * 类描述：接收消息通道
 * 创建人：@author caohui
 * 创建时间：2018/5/13/15:23
 */

public interface InputChannels {

    /**
     * 分析消息通道
     * @Title analy
     * @Description: 分析消息通道
     * @return 消息中间键实例
     * @author: caohui
     * @Date:  2018/5/13/17:15
     */
    @Input("analy")
    SubscribableChannel analy();

    /**
     * 结果处理消息通道
     * @Title result
     * @Description: 结果处理消息通道
     * @return 消息中间键实例
     * @author: caohui
     * @Date:  2018/5/13/17:15
     */
    @Input("result")
    SubscribableChannel result();

}
