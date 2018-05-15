/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/17:20
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskAnalyApplication.java
 * 文件描述: @Description 分析服务器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy;

import com.yhfin.risk.cloud.analy.channel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 分析服务器
 * 包名称：com.yhfin.risk.cloud.analy
 * 类名称：YhRiskAnalyApplication
 * 类描述：分析服务器
 * 创建人：@author caohui
 * 创建时间：2018/5/13/17:20
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages="com.yhfin.risk")
@EnableBinding(InputChannels.class)
@EnableFeignClients
public class YhRiskAnalyApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhRiskAnalyApplication.class, args);
    }
}
