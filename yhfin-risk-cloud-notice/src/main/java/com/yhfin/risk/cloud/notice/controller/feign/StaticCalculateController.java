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
import com.yhfin.risk.cloud.notice.service.local.IOverallManagerService;
import com.yhfin.risk.cloud.notice.service.local.IRequestSupplyService;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.consts.Const;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticAllFundCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

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
	private IOverallManagerService overallManagerService;

	@Autowired
	private IRequestSupplyService requestSupplyService;

	@Autowired
	private IStaticCalculateManageService calculateManageService;

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
	public ServerResponse<?> staticCalculate(@RequestBody StaticCalculateDTO staticCalculate) {
		if (log.isInfoEnabled()) {
			log.info("收到静态计算请求,{}", JSON.toJSONString(staticCalculate));
		}

		String requestId = staticCalculate.getRequestId();
		String serialNumber = requestSupplyService.supplySerialNumber();
		staticCalculate.setSerialNumber(serialNumber);
		if (StringUtils.isBlank(requestId)) {
			requestId = requestSupplyService.supplyRequestId();
		}
		if (calculateManageService.getCalculateProcess()) {
			return ServerResponse.createByError(requestId, serialNumber, Const.ExceptionErrorCode.NOTICE_ERROR_CODE,
					"引擎正处于计算中,不接受计算请求");
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerStaticCalculateRequest(staticCalculate);
		});

		return ServerResponse.createBySuccess(requestId, serialNumber, null);
	}

	/**
	 * 强制结束当前计算
	 *
	 * @Title forceFinishStaticCalculatte
	 * @Description: 强制结束当前计算
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:46:45
	 */
	@RequestMapping(value = "/forceFinish", method = RequestMethod.POST)
	public ServerResponse<?> forceFinishStaticCalculate() {
		String requestId = requestSupplyService.supplyRequestId();
		String serialNumber = "";
		if (calculateManageService.getCalculateProcess()) {
			serialNumber = calculateManageService.getCurrentSerialNumber();
		}
		calculateManageService.forceFinishStaticCalculate();
		return ServerResponse.createBySuccess(requestId, serialNumber);
	}

	/**
	 * 
	 * 查询静态风控状态信息
	 *
	 *
	 * @Title queryStaticCalculateStatus
	 * @Description: 查询静态风控状态信息
	 * @author: caohui
	 * @Date: 2018年5月22日/下午12:30:14
	 */
	@RequestMapping(value = "/queryStaticCalculateStatus", method = RequestMethod.POST)
	public ServerResponse<StaticAllFundCalculateResultDTO> queryStaticCalculateStatus() {
		StaticAllFundCalculateResultDTO staticAllFundCalculateResult = calculateManageService
				.getStaticAllFundCalculateResult();
		if (staticAllFundCalculateResult != null) {
			return ServerResponse.createBySuccess(staticAllFundCalculateResult.getRequestId(),
					staticAllFundCalculateResult.getSerialNumber(), staticAllFundCalculateResult);
		}
		return ServerResponse.createByError("", "", Const.ExceptionErrorCode.NOTICE_ERROR_CODE, "没有计算信息");
	}

}
