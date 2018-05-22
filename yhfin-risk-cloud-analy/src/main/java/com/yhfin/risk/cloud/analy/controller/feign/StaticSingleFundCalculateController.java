/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:33
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: StaticSingleFundCalculateController.java
 * 文件描述: @Description 接收单个基金计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.controller.feign;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.analy.service.local.IOverallManagerService;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

/**
 * 接收单个基金计算请求 包名称：com.yhfin.risk.cloud.analy.controller.feign
 * 类名称：StaticSingleFundCalculateController 类描述：接收单个基金计算请求 创建人：@author caohui
 * 创建时间：2018/5/13/16:33
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/analy")
public class StaticSingleFundCalculateController {

	@Autowired
	private IOverallManagerService overallManagerService;

	/**
	 * 分析服务器接收静态请求信息
	 *
	 * @param singleFundCalculate
	 *            静态请求信息
	 * @return 接口返回信息
	 * @Title StaticSingleFundCalculateDTO
	 * @Description: 分析服务器接收静态请求信息
	 * @author: caohui
	 * @Date: 2018/5/13/16:59
	 */
	@RequestMapping(value = "/staticSingleCalculate", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<String> staticSingleFundCalculate(
			@RequestBody StaticSingleFundCalculateDTO singleFundCalculate) {
		if (log.isInfoEnabled()) {
			log.info(
					StringUtil.commonLogStart(singleFundCalculate.getSerialNumber(), singleFundCalculate.getRequestId())
							+ "接收静态计算请求,{}",
					JSON.toJSONString(singleFundCalculate));
		}
		CompletableFuture.runAsync(() -> {
			overallManagerService.handerStaticSingleFundCalculate(singleFundCalculate);

		});
		return ServerResponse.createBySuccess(singleFundCalculate.getRequestId(),
				singleFundCalculate.getSerialNumber());
	}

}
