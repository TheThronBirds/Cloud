/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:30
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IStaticCalculateManageService.java
 * 文件描述: @Description 静态风控请求计算管理服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local;


import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticAllFundCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;

/**
 * 静态风控请求计算管理服务 包名称：com.yhfin.risk.cloud.notice.service.local
 * 类名称：IStaticCalculateManageService 类描述：静态风控请求计算管理服务 创建人：@author caohui
 * 创建时间：2018/5/13/16:30
 */

public interface IStaticCalculateManageService {

	/**
	 * 处理分析消息
	 *
	 * @param message
	 *            消息
	 * @Title hander
	 * @Description: 处理分析消息
	 * @author: caohui
	 * @Date: 2018/5/13/23:20
	 */
	void handerResultHandleResult(ResultHandleResultDTO message);

	/**
	 * 处理结果消息
	 *
	 * @param message
	 *            消息
	 * @Title hander
	 * @Description: 处理结果消息
	 * @author: caohui
	 * @Date: 2018/5/13/23:20
	 */
	void handerSingleFundAnalyResult(SingleFundAnalyResultDTO message);

	/**
	 * 
	 * 接收到静态请求计算
	 *
	 *
	 * @Title handerStaticCalculate
	 * @Description: 接收到静态请求计算
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:41:11
	 */
	void handerStaticCalculate(StaticCalculateDTO calculate);

	/**
	 * 
	 * 查看当前是否在计算
	 *
	 *
	 * @Title getCalculateProcess
	 * @Description: 查看当前是否在计算
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:49:11
	 */
	boolean getCalculateProcess();

	/**
	 * 查看当前计算版本号
	 *
	 * @Title getCurrentSerialNumber
	 * @Description: 查看当前计算版本号
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:49:25
	 */
	String getCurrentSerialNumber();
	/**
	 * 
	 * 强制停止当前计算
	 *
	 * @Title forceFinishStaticCalculate
	 * @Description: 强制停止当前计算
	 * @author: caohui
	 * @Date: 2018年5月21日/下午3:16:23
	 */
	void forceFinishStaticCalculate();
	
	/**
	 * 获取静态计算状态信息
	 *
	 * @Title getStaticAllFundCalculateResult
	 * @Description: 获取静态计算状态信息
	 * @author: caohui
	 * @Date: 2018年5月22日/下午12:31:17
	 */
	StaticAllFundCalculateResultDTO getStaticAllFundCalculateResult();
}
