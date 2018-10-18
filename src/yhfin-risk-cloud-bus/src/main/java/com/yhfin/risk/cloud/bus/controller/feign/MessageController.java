/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/14:39
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: MessageController.java
 * 文件描述: @Description 接收消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.bus.controller.feign;

import com.yhfin.risk.cloud.bus.service.message.IMessageService;
import com.yhfin.risk.core.common.pojos.dtos.MessageResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.types.ChannelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 接收消息 包名称：com.yhfin.risk.cloud.bus.controller.feign 类名称：MessageController
 * 类描述：接收消息 创建人：@author caohui 创建时间：2018/5/13/14:39
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/bus")
public class MessageController {

	@Autowired
	private IMessageService messageService;

	@RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<MessageResultDTO> outputMessageMemory(@RequestBody MemoryMessageSynchronizateDTO message) {
		if (log.isInfoEnabled()) {
			log.info("收到消息{},消息类型{}", message, message.channelType());
		}
		CompletableFuture.runAsync(()->{
			messageService.sendMessage(message);
		});
		return ServerResponse.createBySuccess(message.getRequestId(),message.getSerialNumber(),null);
	}

	@RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<MessageResultDTO> outputMessageEntry(@RequestBody EntryMessageSynchronizateDTO message) {
		if (log.isInfoEnabled()) {
			log.info("收到消息{},消息类型{}", message, message.channelType());
		}
		CompletableFuture.runAsync(()->{
			messageService.sendMessage(message);
		});
		return ServerResponse.createBySuccess(message.getRequestId(),message.getSerialNumber(),null);
	}


	/**
	 * 发送分析结果信息
	 *
	 * @param message
	 *            消息
	 * @return
	 * @Title entryMessageSynchronizate
	 * @Description: 发送分析结果信息
	 * @author: caohui
	 * @Date: 2018/5/14/0:49
	 */
	@RequestMapping(value = "/analyMessage", method = RequestMethod.POST, produces = "application/json")
	ServerResponse<MessageResultDTO> analyMessage(@RequestBody SingleFundAnalyResultDTO message) {
		if (log.isInfoEnabled()) {
			log.info("收到消息{},消息类型{}", message, message.channelType());
		}
		CompletableFuture.runAsync(()->{
			messageService.sendMessage(message);
		});
		return ServerResponse.createBySuccess(message.getRequestId(),message.getSerialNumber(),null);
	}

	/**
	 * 发送处理结果信息
	 *
	 * @param message
	 *            消息
	 * @return
	 * @Title entryMessageSynchronizate
	 * @Description: 发送处理结果信息
	 * @author: caohui
	 * @Date: 2018/5/14/0:49
	 */
	@RequestMapping(value = "/resultMessage", method = RequestMethod.POST, produces = "application/json")
	ServerResponse<MessageResultDTO> resultMessage(@RequestBody ResultHandleResultDTO message) {
		if (log.isInfoEnabled()) {
			log.info("收到消息{},消息类型{}", message, message.channelType());
		}
		CompletableFuture.runAsync(()->{
			messageService.sendMessage(message);
		});
		return ServerResponse.createBySuccess(message.getRequestId(),message.getSerialNumber(),null);
	}


}
