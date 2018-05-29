/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/17:22
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskNoticeApplication.java
 * 文件描述: @Description 通知服务器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice;

import com.yhfin.risk.cloud.notice.channel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 通知服务器
 * 包名称：com.yhfin.risk.cloud.notice
 * 类名称：YhRiskNoticeApplication
 * 类描述：通知服务器
 * 创建人：@author caohui
 * 创建时间：2018/5/13/17:22
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages="com.yhfin.risk")
@EnableBinding(value = InputChannels.class)
@EnableFeignClients
public class YhRiskNoticeApplication {
    public static void main(String[] args) {
        SpringApplication.run(YhRiskNoticeApplication.class, args);
    }
}
