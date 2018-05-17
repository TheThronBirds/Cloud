/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:03
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: FinalStaticEntryCalculateResultHanderController.java
 * 文件描述: @Description 结果处理
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.result.controller.feign;

import com.yhfin.risk.cloud.result.service.local.IHanderResultService;
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

/**
 * 结果处理 包名称：com.yhfin.risk.cloud.result.controller.feign
 * 类名称：FinalStaticEntryCalculateResultHanderController 类描述：结果处理 创建人：@author
 * caohui 创建时间：2018/5/14/0:03
 */
@RestController
@Slf4j
@RequestMapping("/yhfin/cloud/result")
public class FinalStaticEntryCalculateResultHanderController {

	@Autowired
	private IHanderResultService handerResultService;

	/**
	 * 处理计算结果
	 *
	 * @param calculateResultDTOList
	 *            结果列表
	 * @return
	 * @Title handerResult
	 * @Description: 处理计算结果
	 * @author: caohui
	 * @Date: 2018/5/14/0:06
	 */
	@RequestMapping(value = "/handerResults", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse handerResults(@RequestBody List<FinalStaticEntryCalculateResultDTO> calculateResultDTOList) {
		if (calculateResultDTOList == null || calculateResultDTOList.isEmpty()) {
			return ServerResponse.createBySuccess("", "");
		}
		String requestId = calculateResultDTOList.get(0).getRequestId();
		String serialNumber = calculateResultDTOList.get(0).getSerialNumber();
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到{}条计算结果信息",
					calculateResultDTOList.size());
		}
		handerResultService.handerResults(calculateResultDTOList);
		return ServerResponse.createBySuccess(requestId, serialNumber);
	}

	/**
	 * 处理计算结果
	 *
	 * @param calculateResultDTOList
	 *            结果列表
	 * @return
	 * @Title handerResult
	 * @Description: 处理计算结果
	 * @author: caohui
	 * @Date: 2018/5/14/0:06
	 */
	@RequestMapping(value = "/handerResult", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse handerResult(@RequestBody FinalStaticEntryCalculateResultDTO calculateResultDTO) {
		String requestId = calculateResultDTO.getRequestId();
		String serialNumber = calculateResultDTO.getSerialNumber();
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到一条计算结果信息");
		}
		handerResultService.handerResult(calculateResultDTO);
		return ServerResponse.createBySuccess(requestId, serialNumber);
	}
}
