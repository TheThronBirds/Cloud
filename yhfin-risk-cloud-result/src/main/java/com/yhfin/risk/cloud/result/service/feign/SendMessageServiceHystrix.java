/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:46
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendMessageServiceHystrix.java
 * 文件描述: @Description 发送消息熔断器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.result.service.feign;

import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 发送消息熔断器
 * 包名称：com.yhfin.risk.cloud.result.service.feign
 * 类名称：SendMessageServiceHystrix
 * 类描述：发送消息熔断器
 * 创建人：@author caohui
 * 创建时间：2018/5/14/0:46
 */
@Component
@Slf4j
public class SendMessageServiceHystrix implements FallbackFactory<ISendMessageService> {
    @Override
    public ISendMessageService create(Throwable cause) {
        return new ISendMessageService() {
            @Override
            public ServerResponse resultMessage(ResultHandleResultDTO message) {
                log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
                return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.ExceptionErrorCode.RESULT_ERROR_CODE, "网络断开或处理超时，错误原因" + cause.getMessage(), null);

            }
        };
    }
}