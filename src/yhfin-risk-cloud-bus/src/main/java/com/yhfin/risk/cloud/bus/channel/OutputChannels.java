/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:36
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: OutputChannels.java
 * 文件描述: @Description 消息通道
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.bus.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息通道
 * 包名称：com.yhfin.risk.cloud.bus.channel
 * 类名称：OutputChannels
 * 类描述：消息通道
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:36
 */

public interface OutputChannels {
    /**
     * 发布同步内存数据消息
     *
     * @return
     */
    @Output("memory")
    MessageChannel memory();

    /**
     * 发布同步条目数据消息
     *
     * @return
     */
    @Output("entry")
    MessageChannel entry();

    /**
     * 发布分析信息
     *
     * @return
     */
    @Output("analy")
    MessageChannel analy();

    /**
     * 发布计算信息
     *
     * @return
     */
    @Output("result")
    MessageChannel result();
    


}
