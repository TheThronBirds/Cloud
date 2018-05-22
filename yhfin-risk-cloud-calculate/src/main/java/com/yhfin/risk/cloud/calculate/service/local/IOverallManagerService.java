/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:18:35
 * 项目名称: yhfin-risk-cloud-calculate 
 * 文件名称: IOverallManagerService.java
 * 文件描述: @Description 接收条目同步 内存同步 计算条目统一管理类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.local;

import java.util.List;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;

/**
 * 接收条目同步 内存同步 计算条目统一管理类 包名称：com.yhfin.risk.cloud.calculate.service.local
 * 类名称：IOverallManagerService 类描述：接收条目同步 内存同步 计算条目统一管理类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
public interface IOverallManagerService {

	/**
	 * 处理内存同步消息
	 *
	 * @Title handerMemoryMessageSynchronizate
	 * @Description: 处理内存同步消息
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:41:34
	 */
	void handerMemoryMessageSynchronizate(MemoryMessageSynchronizateDTO memoryMessageSynchronizate);

	/**
	 * 
	 * 处理条目计算结果消息
	 *
	 * @param finalStaticEntryCalculates
	 *            条目初始计算结果信息
	 * @Title handerFinalStaticEntryCalculates
	 * @Description: 处理条目计算结果消息
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:41:57
	 */
	void handerFinalStaticEntryCalculates(List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates);

	/**
	 * 处理条目计算结果消息
	 * 
	 * @param finalStaticEntryCalculate
	 *            条目初始计算结果信息
	 * @Title handerFinalStaticEntryCalculate
	 * @Description: 处理条目计算结果消息
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:42:01
	 */
	void handerFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate);

	/**
	 * 获取当前处理的版本号
	 *
	 * @Title getCurrentSerialNumber
	 * @Description: 获取当前处理的版本号
	 * @author: caohui
	 * @Date: 2018年5月22日/上午10:18:40
	 */
	String getCurrentSerialNumber();

}
