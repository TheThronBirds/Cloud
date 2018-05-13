/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:22
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: YhRiskRegistryApplication.java
 * 文件描述: @Description 风控微服务注册中心
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 风控微服务注册中心
 * 包名称：com.yhfin.risk.cloud.register
 * 类名称：YhRiskRegistryApplication
 * 类描述：风控微服务注册中心
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:22
 */
@SpringBootApplication
@EnableEurekaServer
public class YhRiskRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(YhRiskRegistryApplication.class, args);
    }
}
