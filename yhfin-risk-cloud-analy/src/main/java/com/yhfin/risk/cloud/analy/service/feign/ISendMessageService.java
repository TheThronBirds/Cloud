/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:48
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ISendMessageService.java
 * 文件描述: @Description 发送消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.feign;

import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 发送消息
 * 包名称：com.yhfin.risk.cloud.analy.service.feign
 * 类名称：ISendMessageService
 * 类描述：发送消息
 * 创建人：@author caohui
 * 创建时间：2018/5/14/0:48
 */
@FeignClient(value = "risk-bus", fallbackFactory = SendMessageServiceHystrix.class)
@RequestMapping("/yhfin/cloud/bus")
public interface ISendMessageService {
    /**
     * 发送单个基金分析结果信息
     * @Title entryMessageSynchronizate
     * @Description: 发送单个基金分析结果信息
     * @param  message 消息
     * @return
     * @author: caohui
     * @Date:  2018/5/14/0:49
     */
    @RequestMapping(value = "/analyMessage", method = RequestMethod.POST, produces = "application/json")
    ServerResponse analyMessage(@RequestBody SingleFundAnalyResultDTO message);
}
