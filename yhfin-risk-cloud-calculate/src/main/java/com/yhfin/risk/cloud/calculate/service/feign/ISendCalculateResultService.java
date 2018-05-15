/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:33
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: SendCalculateResultServiceImpl.java
 * 文件描述: @Description 发送计算结果
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.feign;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import org.springframework.cloud.netflix.feign.FeignClient;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 发送计算结果 包名称：com.yhfin.risk.cloud.calculate.service.feign.impl
 * 类名称：SendCalculateResultServiceImpl 类描述：发送计算结果 创建人：@author caohui
 * 创建时间：2018/5/14/0:33
 */
@FeignClient(value = "risk-result", fallbackFactory = SendCalculateResultServiceHystrix.class)
@RequestMapping("/yhfin/cloud/result")
public interface ISendCalculateResultService {

	/**
	 * 发送计算结果给结果处理服务器
	 *
	 * @param finalStaticEntryCalculateResult
	 *            计算
	 * @return
	 * @throws @Title
	 *             sendFinalStaticCalculateResult
	 * @Description: 发送计算结果给结果处理服务器
	 * @author: caohui
	 * @Date: 2018/5/14/0:35
	 */
	@RequestMapping(value = "/handerResult", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse sendFinalStaticCalculateResult(
			@RequestBody FinalStaticEntryCalculateResultDTO finalStaticEntryCalculateResult);

	/**
	 * 发送计算结果给结果处理服务器
	 *
	 * @param finalStaticEntryCalculateResult
	 *            计算
	 * @return
	 * @throws @Title
	 *             sendFinalStaticCalculateResult
	 * @Description: 发送计算结果给结果处理服务器
	 * @author: caohui
	 * @Date: 2018/5/14/0:35
	 */
	@RequestMapping(value = "/handerResults", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse sendFinalStaticCalculateResults(
			@RequestBody List<FinalStaticEntryCalculateResultDTO> finalStaticEntryCalculateResults);
}
