/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月22日/上午11:02:54
 * 项目名称: yhfin-risk-cloud-notice 
 * 文件名称: WebConfig.java
 * 文件描述: @Description restTempalte注入
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * restTempalte注入
 * 包名称：com.yhfin.risk.cloud.notice.config
 * 类名称：WebConfig
 * 类描述：restTempalte注入
 * 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@Configuration
public class WebConfig {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
