/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/26/13:15
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskAnalyCalculateApplication.java
 * 文件描述: @Description 分析计算引擎启动类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.calculate;

import com.yhfin.risk.cloud.analy.calculate.channel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 分析计算引擎启动类
 * 包名称：com.yhfin.risk..cloud.analy.calculate
 * 类名称：YhRiskAnalyCalculateApplication
 * 类描述：分析计算引擎启动类
 * 创建人：@author caohui
 * 创建时间：2018/5/26/13:15
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages="com.yhfin.risk")
@EnableBinding(InputChannels.class)
@EnableFeignClients
public class YhRiskAnalyCalculateApplication {
    public static void main(String[] args) {
        SpringApplication.run(YhRiskAnalyCalculateApplication.class, args);
    }
}
