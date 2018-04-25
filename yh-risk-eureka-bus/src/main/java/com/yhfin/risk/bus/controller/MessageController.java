package com.yhfin.risk.bus.controller;

import com.yhfin.risk.bus.service.ISendMessageService;
import com.yhfin.risk.common.requests.message.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.types.ChannelType;
import com.yhfin.risk.common.utils.StringUtil;

/**
 * 接收消息,向消息中间键发布消息
 *
 * @author youlangta
 * @since 2018-03-20
 */
@RestController
@RequestMapping("/yhfin/bus")
public class MessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISendMessageService messageService;

    @RequestMapping(value = "/riskMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<RiskMessageSynchronizate> outputMessageRisk(@RequestBody RiskMessageSynchronizate message) {
        return outMessage(message, ChannelType.RISK);
    }

    @RequestMapping(value = "/analyMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<AnalyMessageSynchronizate> outputMessageAnaly(@RequestBody AnalyMessageSynchronizate message) {
        return outMessage(message, ChannelType.ANALY);
    }

    @RequestMapping(value = "/calculateMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<CalculateMessageSynchronizate> outputMessageCalculate(@RequestBody CalculateMessageSynchronizate message) {
        return outMessage(message, ChannelType.CALCULATE);
    }

    @RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<MemoryMessageSynchronizate> outputMessageMemory(@RequestBody MemoryMessageSynchronizate message) {
        return outMessage(message, ChannelType.MEMORY);
    }

    @RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<EntryMessageSynchronizate> outputMessageEntry(@RequestBody EntryMessageSynchronizate message) {
        return outMessage(message, ChannelType.ENTRY);
    }

    @RequestMapping(value = "/queryCalculateMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<EntryMessageSynchronizate> outputMessageQueryCalculate(@RequestBody EntryMessageSynchronizate message) {
        return outMessage(message, ChannelType.QUERY_CALCULATE);
    }

    @RequestMapping(value = "/resultMessageSynchronizate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<ResultMessageSynchronizate> outputMessageResult(@RequestBody ResultMessageSynchronizate message) {
        return outMessage(message, ChannelType.RESULT);
    }

    /**
     * 判断请求发布消息参数是否证券
     *
     * @param message 发布消息体
     * @return
     */
    private ServerResponse checkMessage(AbstractBaseMessageRequest message, ChannelType channelType) {
        if (message == null) {
            return ServerResponse.createByError("", "", Const.exceptionErrorCode.STREAM_ERROR_CODE, "发布消息体为空");
        }

        if (StringUtils.isBlank(message.getRequestId()) || StringUtils.isBlank(message.getSerialNumber())) {
            return ServerResponse.createByError("", "", Const.exceptionErrorCode.STREAM_ERROR_CODE, "请求序号为空或者流水号为空");
        }

        if (message.getChannelType() == null) {
            return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(),
                    Const.exceptionErrorCode.STREAM_ERROR_CODE, "消息体类型为空");
        }

        if (message.getChannelType() != channelType) {
            return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(),
                    Const.exceptionErrorCode.STREAM_ERROR_CODE, "消息体类型接口错误,接口消息类型为"
                            + message.getChannelType().getTypeDes() + ",发布消息的消息类型为" + channelType.getTypeDes());
        }

        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + ",请求发布消息{},消息类型{}", message.getSerialNumber(),
                    message.getRequestId(), JSON.toJSONString(message), message.getChannelType().getTypeDes());
        }
        return ServerResponse.createBySuccess(message.getRequestId(), message.getSerialNumber(), message);
    }

    private ServerResponse outMessage(AbstractBaseMessageRequest message, ChannelType channelType) {
        ServerResponse messageResult = checkMessage(message, channelType);
        if (!messageResult.isSuccess()) {
            return messageResult;
        }
        boolean sendValidFlag = messageService.sendMessage(message, message.getChannelType());
        if (logger.isInfoEnabled()) {
            String result = sendValidFlag ? "成功" : "失败";
            logger.info(StringUtil.commonLogStart() + ",发布消息{},消息类型{},结果:" + result, message.getSerialNumber(),
                    message.getRequestId(), JSON.toJSONString(message), message.getChannelType().getTypeDes());
        }
        return messageResult;
    }
}
