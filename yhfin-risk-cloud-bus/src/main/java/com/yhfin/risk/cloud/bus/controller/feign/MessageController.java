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

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.bus.service.message.IMessageService;
import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.AbstractMessageDTO;
import com.yhfin.risk.core.common.pojos.dtos.MessageResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.types.ChannelTypeEnum;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收消息
 * 包名称：com.yhfin.risk.cloud.bus.controller.feign
 * 类名称：MessageController
 * 类描述：接收消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/14:39
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/bus")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<MessageResultDTO> outputMessageMemory(@RequestBody MemoryMessageSynchronizateDTO message) {
        return outMessage(message, ChannelTypeEnum.MEMORY);
    }

    @RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<MessageResultDTO> outputMessageEntry(@RequestBody EntryMessageSynchronizateDTO message) {
        return outMessage(message, ChannelTypeEnum.ENTRY);
    }

    private ServerResponse<MessageResultDTO> outMessage(AbstractMessageDTO message, ChannelTypeEnum channelType) {
        ServerResponse<MessageResultDTO> messageResult = checkMessage(message, channelType);
        if (!messageResult.isSuccess()) {
            return messageResult;
        }
        boolean sendValidFlag = messageService.sendMessage(message, message.getChannelType());
        messageResult.getData().setSendSuccess(sendValidFlag);
        if (log.isInfoEnabled()) {
            String result = sendValidFlag ? "成功" : "失败";
            log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",发布消息{},消息类型{},结果:" + result, JSON.toJSONString(message), message.getChannelType().getTypeDes());
        }
        return messageResult;
    }

    /**
     * 校验消息
     * @Title checkMessage 校验消息
     * @Description: 校验消息
     * @param  message 消息体
     * @return 校验结果
     * @throws
     * @author: caohui
     * @Date:  2018/5/13/15:07
     */
    private ServerResponse<MessageResultDTO> checkMessage(AbstractMessageDTO message, ChannelTypeEnum channelType) {
        MessageResultDTO result = new MessageResultDTO();
        result.setChannelType(channelType);
        result.setRequestId(message.getRequestId());
        result.setSendSuccess(false);
        result.setSerialNumber(message.getSerialNumber());
        if (message == null) {
            return ServerResponse.createByError("", "", Const.ExceptionErrorCode.BUS_ERROR_CODE, "发布消息体为空",result);
        }

        if (StringUtils.isBlank(message.getRequestId()) || StringUtils.isBlank(message.getSerialNumber())) {
            return ServerResponse.createByError("", "", Const.ExceptionErrorCode.BUS_ERROR_CODE, "请求序号为空或者流水号为空",result);
        }

        if (message.getChannelType() == null) {
            return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(),
                    Const.ExceptionErrorCode.BUS_ERROR_CODE, "消息体类型为空",result);
        }

        if (message.getChannelType() != channelType) {
            return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(),
                    Const.ExceptionErrorCode.BUS_ERROR_CODE, "消息体类型接口错误,接口消息类型为"
                            + message.getChannelType().getTypeDes() + ",发布消息的消息类型为" + channelType.getTypeDes());
        }
        if (log.isInfoEnabled()) {
            log.info(StringUtil.commonLogStart(message.getSerialNumber(),
                    message.getRequestId()) + ",请求发布消息{},消息类型{}", JSON.toJSONString(message), message.getChannelType().getTypeDes());
        }
        return ServerResponse.createBySuccess(message.getRequestId(), message.getSerialNumber(), result);
    }


}
