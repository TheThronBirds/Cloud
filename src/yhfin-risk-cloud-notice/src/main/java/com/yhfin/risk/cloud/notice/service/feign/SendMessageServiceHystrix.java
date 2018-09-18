/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:43
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendMessageServiceHystrix.java
 * 文件描述: @Description 发送消息断路器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.feign;

import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.MessageResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 发送消息断路器
 * 包名称：com.yhfin.risk.cloud.notice.service.feign
 * 类名称：SendMessageServiceHystrix
 * 类描述：发送消息断路器
 * 创建人：@author caohui
 * 创建时间：2018/5/13/15:43
 */
@Slf4j
@Component
public class SendMessageServiceHystrix implements FallbackFactory<ISendMessageService> {
    @Override
    public ISendMessageService create(Throwable cause) {
        return new ISendMessageService() {

            /**
             * 发送条目同步消息
             *
             * @param message 消息体
             * @return 接口返回信息
             * @Title entryMessageSynchronizate
             * @Description: 发送条目同步消息
             * @author: caohui
             * @Date: 2018/5/13/15:37
             */
            @Override
            public ServerResponse<MessageResultDTO> entryMessageSynchronizate(EntryMessageSynchronizateDTO message) {
                log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
                return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.ExceptionErrorCode.NOTICE_ERROR_CODE,"网络断开或处理超时，错误原因" + cause.getMessage(), null);

            }

            /**
             * 发送内存同步消息
             *
             * @param message 消息体
             * @return 接口返回信息
             * @Title memoryMessageSynchronizate
             * @Description: 发送内存同步消息
             * @author: caohui
             * @Date: 2018/5/13/15:37
             */
            @Override
            public ServerResponse<MessageResultDTO> memoryMessageSynchronizate(MemoryMessageSynchronizateDTO message) {
                log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
                return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.ExceptionErrorCode.NOTICE_ERROR_CODE,"网络断开或处理超时，错误原因" + cause.getMessage(), null);
            }
        };
    }
}
