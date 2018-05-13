/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:24
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendStaticSingleFundCalculateServiceHystrix.java
 * 文件描述: @Description 发送单个基金计算请求熔断器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.feign;

import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 发送单个基金计算请求熔断器
 * 包名称：com.yhfin.risk.cloud.notice.service.feign
 * 类名称：SendStaticSingleFundCalculateServiceHystrix
 * 类描述：发送单个基金计算请求熔断器
 * 创建人：@author caohui
 * 创建时间：2018/5/13/16:24
 */
@Component
@Slf4j
public class SendStaticSingleFundCalculateServiceHystrix implements FallbackFactory<ISendStaticSingleFundtCalculateService> {
    @Override
    public ISendStaticSingleFundtCalculateService create(Throwable cause) {
        return new ISendStaticSingleFundtCalculateService(){
            @Override
            public ServerResponse<String> staticSingleFundCalculate(StaticSingleFundCalculateDTO singleFundCalculate) {
                log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
                return ServerResponse.createByError(singleFundCalculate.getRequestId(), singleFundCalculate.getSerialNumber(), Const.ExceptionErrorCode.NOTICE_ERROR_CODE,"网络断开或处理超时，错误原因" + cause.getMessage(), null);

            }
        };
    }
}
