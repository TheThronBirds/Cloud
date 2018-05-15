/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月15日/下午3:55:16
 * 项目名称: yhfin-risk-cloud-notice 
 * 文件名称: StaticCalculateManagerController.java
 * 文件描述: @Description 消息通知服务状态管理接口
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.controller.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.reponse.ServerResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息通知服务状态管理接口 包名称：com.yhfin.risk.cloud.notice.controller.feign
 * 类名称：StaticCalculateManagerController 类描述：消息通知服务状态管理接口 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@RestController
@Slf4j
public class StaticCalculateManagerController {
	@Autowired
	private IStaticCalculateManageService manageService;
	
	/**
	 * 
	 * 初始化通知服务中心静态计算状态
	 *
	 *
	 * @Title initNoticeState
	 * @Description: 初始化通知服务中心静态计算状态
	 * @author: caohui
	 * @Date: 2018年5月15日/下午3:57:34
	 */
	@RequestMapping(value = "/initNoticeState", method = RequestMethod.POST)
	public ServerResponse initNoticeState() {
		if(log.isInfoEnabled()){
			log.info("初始化通知服务中心静态计算状态");
		}
		manageService.initNoticeState();
		return ServerResponse.createBySuccess("", "");
	}
}
