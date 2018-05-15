/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/23:27
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IManageService.java
 * 文件描述: @Description 处理计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;

import java.util.List;

/**
 * 处理计算请求 包名称：com.yhfin.risk.cloud.analy.service.local 类名称：IManageService
 * 类描述：处理计算请求 创建人：@author caohui 创建时间：2018/5/13/23:27
 */

public interface IManageService {

	/**
	 * 发送计算请求
	 *
	 * @param finalStaticEntryCalculates
	 *            计算请求
	 * @return
	 * @throws @Title
	 *             sendFinalStaticEntryCalculates
	 * @Description: 发送计算请求
	 * @author: caohui
	 * @Date: 2018/5/13/23:33
	 */
	ServerResponse sendFinalStaticEntryCalculates(List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates);

	/**
	 * 发送计算请求
	 *
	 * @param finalStaticEntryCalculate
	 *            计算请求
	 * @return
	 * @Title sendFinalStaticEntryCalculates
	 * @Description: 发送计算请求
	 * @author: caohui
	 * @Date: 2018/5/13/23:33
	 */
	ServerResponse sendFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate);
}
