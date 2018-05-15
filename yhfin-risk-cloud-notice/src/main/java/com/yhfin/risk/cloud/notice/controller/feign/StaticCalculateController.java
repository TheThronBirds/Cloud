/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:52
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: StaticCalculateController.java
 * 文件描述: @Description 接收静态计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.controller.feign;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.cloud.notice.service.feign.ISendStaticSingleFundtCalculateService;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateFinalDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接收静态计算请求 包名称：com.yhfin.risk.cloud.notice.controller.feign
 * 类名称：StaticCalculateController 类描述：接收静态计算请求 创建人：@author caohui
 * 创建时间：2018/5/13/15:52
 */
@RestController
@RequestMapping("/yhfin/cloud/notice")
@Slf4j
public class StaticCalculateController {

	@Autowired
	private ISendStaticSingleFundtCalculateService sendStaticSingleFundtCalculateService;

	@Autowired
	private IStaticCalculateManageService staticCalculateManageService;

	/**
	 * 接收静态请求，根据基金轮训发送到分析服务器
	 *
	 * @param staticCalculate
	 *            静态风控请求
	 * @return 返回发送到分析服务器接收结果
	 * @Title staticCalculate
	 * @Description: 接收静态请求，根据基金轮询发送到分析服务器
	 * @author: caohui
	 * @Date: 2018/5/13/17:12
	 */
	@RequestMapping(value = "/staticCalculate", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "staticCalculateFallBack")
	public ServerResponse<StaticCalculateFinalDTO> staticCalculate(@RequestBody StaticCalculateDTO staticCalculate) {
		if (log.isInfoEnabled()) {
			log.info("收到静态计算请求,{}", JSON.toJSONString(staticCalculate));
		}
		List<StaticSingleFundCalculateDTO> staticSingleFundCalculateRequests = staticCalculate.getFundIds().stream()
				.parallel().map(item -> {
					return new StaticSingleFundCalculateDTO(staticCalculate.getRequestId(),
							staticCalculate.getSerialNumber(), item, staticCalculate.getRiskIds());
				}).collect(Collectors.toList());
		if (staticSingleFundCalculateRequests == null || staticSingleFundCalculateRequests.isEmpty()) {
			return ServerResponse.createByError(staticCalculate.getRequestId(), staticCalculate.getSerialNumber(),
					Const.ExceptionErrorCode.NOTICE_ERROR_CODE, "没有有效基金发起静态请求");
		}

		StaticCalculateFinalDTO calculateResult = new StaticCalculateFinalDTO(staticCalculate.getRequestId(),
				staticCalculate.getSerialNumber());
		staticCalculateManageService.initStaticManage(staticSingleFundCalculateRequests, staticCalculate.getRequestId(),
				staticCalculate.getSerialNumber());
		for (StaticSingleFundCalculateDTO singleFundCalculate : staticSingleFundCalculateRequests) {
			ServerResponse<String> stringServerResponse = sendStaticSingleFundtCalculateService
					.staticSingleFundCalculate(singleFundCalculate);
			if (stringServerResponse.isSuccess()) {
				staticCalculateManageService.hander(singleFundCalculate.getFundId(), true,
						staticCalculate.getRequestId(), staticCalculate.getSerialNumber());
				if (calculateResult.getSuccessFundIds() == null) {
					calculateResult.setSuccessFundIds(new ArrayList<>(600));
				}
				calculateResult.getSuccessFundIds().add(singleFundCalculate.getFundId());
			} else {
				staticCalculateManageService.hander(singleFundCalculate.getFundId(), false,
						staticCalculate.getRequestId(), staticCalculate.getSerialNumber());
				if (calculateResult.getErrorFundIds() == null) {
					calculateResult.setErrorFundIds(new ArrayList<>(100));
				}
				calculateResult.getErrorFundIds().add(singleFundCalculate.getFundId());
			}
		}

		return ServerResponse.createBySuccess(staticCalculate.getRequestId(), staticCalculate.getSerialNumber(),
				calculateResult);
	}

	public ServerResponse<StaticCalculateFinalDTO> staticCalculateFallBack(StaticCalculateDTO calculateRequest,
			Throwable e) {
		if (log.isErrorEnabled()) {
			log.error(StringUtil.commonLogStart(calculateRequest.getSerialNumber(), calculateRequest.getRequestId())
					+ "静态风控请求发生错误," + e.getMessage());
			log.error("" + e, e);
		}
		
	
		return ServerResponse.createByError(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(),
				Const.ExceptionErrorCode.NOTICE_ERROR_CODE, e.getMessage());
	}
}
