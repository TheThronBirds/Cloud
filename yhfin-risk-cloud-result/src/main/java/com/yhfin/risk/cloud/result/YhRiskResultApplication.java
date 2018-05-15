/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:01
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskResultApplication.java
 * 文件描述: @Description 结果处理服务器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.result;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 结果处理服务器
 * 包名称：com.yhfin.risk.cloud.result
 * 类名称：YhRiskResultApplication
 * 类描述：结果处理服务器
 * 创建人：@author caohui
 * 创建时间：2018/5/14/0:01
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages="com.yhfin.risk")
@EnableFeignClients
public class YhRiskResultApplication {
    public static void main(String[] args) {
        SpringApplication.run(YhRiskResultApplication.class, args);
    }
}
