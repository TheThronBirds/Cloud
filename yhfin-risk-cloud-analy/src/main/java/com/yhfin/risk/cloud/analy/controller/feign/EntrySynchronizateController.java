/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:22:44
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: EntrySynchronizateController.java
 * 文件描述: @Description 条目同步controller类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.controller.feign;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.analy.service.local.IOverallManagerService;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 条目同步controller类 包名称：com.yhfin.risk.cloud.analy.controller.feign
 * 类名称：EntrySynchronizateController 类描述：条目同步controller类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/analy")
public class EntrySynchronizateController {

	@Autowired
	private IOverallManagerService overallManagerService;

	@RequestMapping(value = "/entrySynchronizate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse entrySynchronizate(@RequestBody EntryMessageSynchronizateDTO entryMessage) {
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(entryMessage.getSerialNumber(), entryMessage.getRequestId())
					+ ",接收到同步条目消息,{}", JSON.toJSONString(entryMessage));
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerEntryMessageSynchronizate(entryMessage);
		});
		return ServerResponse.createBySuccess(entryMessage.getRequestId(), entryMessage.getSerialNumber());
	}
}
