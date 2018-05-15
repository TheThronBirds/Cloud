/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月15日/下午1:38:50
 * 项目名称: yhfin-risk-cloud-calculate 
 * 文件名称: SendCalculateResultServiceHystrix.java
 * 文件描述: @Description 发送结果处理给结果处理服务器熔断器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.feign;

import java.util.List;

import org.springframework.stereotype.Component;

import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送结果处理给结果处理服务器熔断器 包名称：com.yhfin.risk.cloud.calculate.service.feign
 * 类名称：SendCalculateResultServiceHystrix 类描述：发送结果处理给结果处理服务器熔断器 创建人：@author
 * caohui 创建时间：2018/5/13/12:20
 */
@Component
@Slf4j
public class SendCalculateResultServiceHystrix implements FallbackFactory<ISendCalculateResultService> {

	@Override
	public ISendCalculateResultService create(Throwable cause) {
		return new ISendCalculateResultService() {

			@Override
			public ServerResponse sendFinalStaticCalculateResult(
					FinalStaticEntryCalculateResultDTO finalStaticEntryCalculateResult) {
				String serialNumber = finalStaticEntryCalculateResult.getSerialNumber();
				String requestId = finalStaticEntryCalculateResult.getRequestId();
				log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
				return ServerResponse.createByError(requestId, serialNumber,
						Const.ExceptionErrorCode.CALCULATE_ERROR_CODE, "服务不存在或网络断开," + cause.getMessage());
			}

			@Override
			public ServerResponse sendFinalStaticCalculateResults(
					List<FinalStaticEntryCalculateResultDTO> finalStaticEntryCalculateResults) {
				String serialNumber = finalStaticEntryCalculateResults.get(0).getSerialNumber();
				String requestId = finalStaticEntryCalculateResults.get(0).getRequestId();
				log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
				return ServerResponse.createByError(requestId, serialNumber,
						Const.ExceptionErrorCode.CALCULATE_ERROR_CODE, "服务不存在或网络断开," + cause.getMessage());
			}

		};
	}

}
