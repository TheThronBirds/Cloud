/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:35
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskBusApplication.java
 * 文件描述: @Description 消息中间键服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.bus;

import com.yhfin.risk.cloud.bus.channel.OutputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 消息中间键服务
 * 包名称：com.yhfin.risk.cloud.bus
 * 类名称：YhRiskBusApplication
 * 类描述：消息中间键服务
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:35
 */

@SpringCloudApplication
@EnableBinding(OutputChannels.class)
public class YhRiskBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhRiskBusApplication.class, args);
    }
}
