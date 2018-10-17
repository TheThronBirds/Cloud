/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/20:53
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendMessageController.java
 * 文件描述: @Description 接收消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.controller.feign;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yhfin.risk.cloud.notice.service.local.IOverallManagerService;
import com.yhfin.risk.cloud.notice.service.local.IRequestSupplyService;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 接收消息 包名称：com.yhfin.risk.cloud.notice.controller.feign
 * 类名称：SendMessageController 类描述：接收消息 创建人：@author caohui 创建时间：2018/5/13/20:53
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/notice")
public class SendMessageController {

	@Autowired
	private IOverallManagerService overallManagerService;

	@Autowired
	private IRequestSupplyService requestSupplyService;

	@Autowired
	private IStaticCalculateManageService calculateManageService;

	/**
	 * 发送条目同步消息
	 *
	 * @param message
	 *            消息体
	 * @return 接口返回信息
	 * @Title entryMessageSynchronizate
	 * @Description: 发送条目同步消息
	 * @author: caohui
	 * @Date: 2018/5/13/15:37
	 */
	@RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
	ServerResponse<?> entryMessageSynchronizate(@RequestBody EntryMessageSynchronizateDTO message) {
		if (log.isInfoEnabled()) {
			log.info("通知中心接收到同步条目消息请求");
		}
		String requestId = message.getRequestId();
		String serialNumber = requestSupplyService.supplySerialNumber();

		if (StringUtils.isBlank(requestId)) {
			requestId = requestSupplyService.supplyRequestId();
		}
		message.setRequestId(requestId);
		message.setSerialNumber(serialNumber);
		if (calculateManageService.getCalculateProcess()) {
			return ServerResponse.createByError(requestId, serialNumber, Const.ExceptionErrorCode.NOTICE_ERROR_CODE,
					"引擎正处于计算中,不接受同步请求");
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerEntrySynchronizateMessage(message);
		});
		return ServerResponse.createBySuccess(requestId, serialNumber, null);
	}

	/**
	 * 发送内存同步消息
	 *
	 * @param message
	 *            消息体
	 * @return 接口返回信息
	 * @Title memoryMessageSynchronizate
	 * @Description: 发送内存同步消息
	 * @author: caohui
	 * @Date: 2018/5/13/15:37
	 */
	@RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
	ServerResponse<?> memoryMessageSynchronizate(@RequestBody MemoryMessageSynchronizateDTO message) {
		if (log.isInfoEnabled()) {
			log.info("通知中心接收到同步内存消息请求");
		}
		String requestId = message.getRequestId();
		String serialNumber = requestSupplyService.supplySerialNumber();
		if (StringUtils.isBlank(requestId)) {
			requestId = requestSupplyService.supplyRequestId();
		}
		message.setRequestId(requestId);
		message.setSerialNumber(serialNumber);

		if (calculateManageService.getCalculateProcess()) {
			return ServerResponse.createByError(requestId, serialNumber, Const.ExceptionErrorCode.NOTICE_ERROR_CODE,
					"引擎正处于计算中,不接受同步请求");
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerMemorySynchronizateMessage(message);
		});
		return ServerResponse.createBySuccess(requestId, serialNumber, null);
	}

	/**
	 * 发送内存同步状态查询消息
	 *
	 *            消息体
	 * @return 接口返回信息
	 * @Title memoryMessageSynchronizateStatus
	 * @Description: 发送内存同步消息
	 * @author: benguolong
	 * @Date: 2018/5/13/15:37
	 */
	@RequestMapping(value = "/memoryMessageSynchronizateStatus", method = RequestMethod.POST, produces = "application/json")
	ServerResponse<?> memoryMessageSynchronizate() {
		if (log.isInfoEnabled()) {
			log.info("通知中心接收到同步内存状态查询消息请求");
		}
		if (calculateManageService.getCalculateProcess()) {
			return ServerResponse.createByError("", "", Const.ExceptionErrorCode.NOTICE_ERROR_CODE,
					"引擎正处于计算中,不接受内存同步查询请求");
		}
		List<Object> obj = null;
		try {
			obj = overallManagerService.handerMemorySynchronizateStatusMessage();
		} catch (Exception e) {
			ServerResponse.createByError("", "", Const.ExceptionErrorCode.ELSE_ERROR_CODE,
					"同步内存状态查询异常",e);
		}
		return ServerResponse.createBySuccess("", "", obj);
	}
}
