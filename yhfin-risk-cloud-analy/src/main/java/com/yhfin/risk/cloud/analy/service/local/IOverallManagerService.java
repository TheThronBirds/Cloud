/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:15:53
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: IOverallManagerService.java
 * 文件描述: @Description 全局管理同步内存 同步条目 接收计算请求管理类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local;

import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;

/**
 * 全局管理同步内存 同步条目 接收计算请求管理类 包名称：com.yhfin.risk.cloud.analy.service.local
 * 类名称：IOverallManagerService 类描述：全局管理同步内存 同步条目 接收计算请求管理类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
public interface IOverallManagerService {
	/**
	 * 
	 * 接收同步内存请求
	 *
	 *
	 * @Title handerMemoryMessageSynchronizate
	 * @Description: 接收同步内存请求
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:30:57
	 */
	void handerMemoryMessageSynchronizate(MemoryMessageSynchronizateDTO memoryMessageSynchronizate);
	/**
	 * 接收同步条目请求
	 *
	 * @Title handerEntryMessageSynchronizate
	 * @Description: 接收同步条目请求
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:31:11
	 */
	void handerEntryMessageSynchronizate(EntryMessageSynchronizateDTO entryMessageSynchronizate);
	/**
	 * 接收静态计算请求
	 *
	 * @Title handerStaticSingleFundCalculate
	 * @Description: 接收静态计算请求
	 * @author: caohui
	 * @Date: 2018年5月21日/下午4:31:47
	 */
	void handerStaticSingleFundCalculate(StaticSingleFundCalculateDTO singleFundCalculate);
	
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
