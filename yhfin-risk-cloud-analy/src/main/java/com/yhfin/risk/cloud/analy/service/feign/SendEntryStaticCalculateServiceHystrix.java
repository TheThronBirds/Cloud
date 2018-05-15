/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月15日/下午1:14:33
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: SendEntryStaticCalculateServiceHystrix.java
 * 文件描述: @Description 发送计算请求给计算服务器熔断器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.feign;

import java.util.List;

import org.springframework.stereotype.Component;

import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送计算请求给计算服务器熔断器 包名称：com.yhfin.risk.cloud.analy.service.feign
 * 类名称：SendEntryStaticCalculateServiceHystrix 类描述：发送计算请求给计算服务器熔断器 创建人：@author
 * caohui 创建时间：2018/5/13/12:20
 */
@Component
@Slf4j
public class SendEntryStaticCalculateServiceHystrix implements FallbackFactory<ISendEntryStaticCalculateService> {

	@Override
	public ISendEntryStaticCalculateService create(Throwable cause) {
		return new ISendEntryStaticCalculateService() {

			@Override
			public ServerResponse<String> consiseCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
				log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
				return ServerResponse.createByError(
						finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getRequestId(),
						finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber(),
						Const.ExceptionErrorCode.ANALY_ERROR_CODE, "网络断开或处理超时，错误原因" + cause.getMessage(), null);

			}

			@Override
			public ServerResponse<String> consiseCalculates(
					List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates) {
				log.error("服务不存在或网络断开,错误原因:{}", cause.getMessage());
				return ServerResponse.createByError(
						finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getRequestId(),
						finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getSerialNumber(),
						Const.ExceptionErrorCode.ANALY_ERROR_CODE, "网络断开或处理超时，错误原因" + cause.getMessage(), null);

			}

		};
	}

}
