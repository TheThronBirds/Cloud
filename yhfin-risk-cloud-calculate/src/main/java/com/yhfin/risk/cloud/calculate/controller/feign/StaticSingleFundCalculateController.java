/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/23:51
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: StaticSingleFundCalculateController.java
 * 文件描述: @Description 接收计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.controller.feign;

import com.yhfin.risk.cloud.calculate.service.feign.ISendCalculateResultService;
import com.yhfin.risk.cloud.calculate.service.local.ICaculateResultManageService;
import com.yhfin.risk.core.calculate.reduce.ICalculateService;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.*;

/**
 * 接收计算请求 包名称：com.yhfin.risk.cloud.calculate.controller.feign
 * 类名称：StaticSingleFundCalculateController 类描述：接收计算请求 创建人：@author caohui
 * 创建时间：2018/5/13/23:51
 */
@RestController
@RequestMapping("/yhfin/cloud/calculate")
@Slf4j
public class StaticSingleFundCalculateController {

	@Autowired
	private ICalculateService calculateService;

	@Autowired
	private ICaculateResultManageService caculateResultManageService;

	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 2,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

	@RequestMapping(value = "/consiseCalculates", method = RequestMethod.POST)
	public ServerResponse<String> consiseCalculates(
			@RequestBody List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates) {
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(
					finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getSerialNumber(),
					finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getRequestId())
					+ "接收到{}条计算请求", finalStaticEntryCalculates.size());
		}
		String serialNumber = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getSerialNumber();
		String requestId = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getRequestId();

		CompletableFuture.runAsync(() -> {
			finalStaticEntryCalculates.stream().parallel().forEach((item) -> {
				CompletableFuture.runAsync(() -> {
					caculateResultManageService
							.putFinalStaticEntryCalculateResultDTOs(calculateService.calculateRequest(item));
				}, executorService);
			});
		}, executorService);

		return ServerResponse.createBySuccess(requestId, serialNumber, "");
	}

	@RequestMapping(value = "/consiseCalculate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<String> consiseCalculate(
			@RequestBody FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(
					finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber(),
					finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getRequestId()) + "接收到条目静态计算请求");
		}
		String serialNumber = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber();
		String requestId = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getRequestId();
		CompletableFuture.runAsync(() -> {
			FinalStaticEntryCalculateResultDTO finalStaticEntryCalculateResult = calculateService
					.calculateRequest(finalStaticEntryCalculate);
			caculateResultManageService.putFinalStaticEntryCalculateResultDTOs(finalStaticEntryCalculateResult);
		}, executorService);
		return ServerResponse.createBySuccess(requestId, serialNumber);
	}
}
