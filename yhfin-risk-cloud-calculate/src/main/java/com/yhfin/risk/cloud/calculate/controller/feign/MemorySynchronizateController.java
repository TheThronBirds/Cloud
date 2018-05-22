/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:23:31
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: MemorySynchronizateController.java
 * 文件描述: @Description 内存同步controller类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.controller.feign;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.calculate.service.local.IOverallManagerService;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 内存同步controller类 包名称：com.yhfin.risk.cloud.analy.controller.feign
 * 类名称：MemorySynchronizateController 类描述：内存同步controller类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/calculate")
public class MemorySynchronizateController {

	@Autowired
	private IOverallManagerService overallManagerService;

	@RequestMapping(value = "/memorySynchronizate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse memorySynchronizate(@RequestBody MemoryMessageSynchronizateDTO memoryMessage) {
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(memoryMessage.getSerialNumber(), memoryMessage.getRequestId())
					+ ",接收到同步内存消息,{}", JSON.toJSONString(memoryMessage));
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerMemoryMessageSynchronizate(memoryMessage);
		});
		return ServerResponse.createBySuccess(memoryMessage.getRequestId(), memoryMessage.getSerialNumber());
	}
}
