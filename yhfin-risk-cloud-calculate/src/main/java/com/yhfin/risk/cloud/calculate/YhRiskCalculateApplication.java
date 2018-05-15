/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/17:21
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskCalculateApplication.java
 * 文件描述: @Description 计算微服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate;

import com.yhfin.risk.cloud.calculate.channel.InputChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 计算微服务
 * 包名称：com.yhfin.risk.cloud.calculate
 * 类名称：YhRiskCalculateApplication
 * 类描述：计算微服务
 * 创建人：@author caohui
 * 创建时间：2018/5/13/17:21
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages="com.yhfin.risk")
@EnableBinding(InputChannels.class)
@EnableFeignClients
public class YhRiskCalculateApplication {
    public static void main(String[] args) {
        SpringApplication.run(YhRiskCalculateApplication.class, args);
    }
}
