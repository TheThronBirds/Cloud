/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/上午9:29:44
 * 项目名称: yhfin-risk-cloud-notice 
 * 文件名称: IOverallManagerService.java
 * 文件描述: @Description 把通知中心接收 的消息放入队列中，按照顺序一个一个进行处理；通过轮询方式发送同步内存 同步条目消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local;

import java.util.List;

import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;

/**
 *   把通知中心接收 的消息放入队列中，按照顺序一个一个进行处理；通过轮询方式发送同步内存 同步条目消息
 * 包名称：com.yhfin.risk.cloud.notice.service.local
 * 类名称：IOverallManagerService
 * 类描述：通过轮询方式发送同步内存 同步条目消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
public interface IOverallManagerService {
	
	/**
	 * 接收条目同步消息，通过轮询方式基金处理
	 * @Title handerEntrySynchronizateMessage
	 * @Description: 接收条目同步消息，通过轮询方式基金处理
	 * @author: caohui
	 * @Date: 2018年5月21日/上午11:06:46
	 */
	void handerEntrySynchronizateMessage(EntryMessageSynchronizateDTO message);
	/**
	 * 
	 * 接收内存同步消息，通过轮询方式进行处理
	 * @Title handerMemorySynchronizateMessage
	 * @Description: 接收内存同步消息，通过轮询方式进行处理
	 * @author: caohui
	 * @Date: 2018年5月21日/上午11:07:11
	 */
	void handerMemorySynchronizateMessage(MemoryMessageSynchronizateDTO message);
	
	/**
	 * 对接收的静态请求进行处理
	 *
	 * @Title handerStaticCalculateRequest
	 * @Description: 对接收的静态请求进行处理
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:30:01
	 */
	void handerStaticCalculateRequest(StaticCalculateDTO staticCalculate);
	
	/**
	 * 接收内存同步状态查询消息
	 *
	 * @Title handerMemorySynchronizateStatusMessage
	 * @Description: 接收内存同步状态查询消息
	 * @author: benguolong
	 * @Date: 2018年8月29日/下午18:30:01
	 */
	List<Object> handerMemorySynchronizateStatusMessage();

	
}
