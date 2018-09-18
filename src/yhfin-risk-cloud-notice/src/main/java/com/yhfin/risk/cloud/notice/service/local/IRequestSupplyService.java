/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午1:10:55
 * 项目名称: yhfin-risk-cloud-notice 
 * 文件名称: IRequestSupplyService.java
 * 文件描述: @Description 生产请求序号  流水号
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local;

/**
 * 生产请求序号 流水号 包名称：com.yhfin.risk.cloud.notice.service.local
 * 类名称：IRequestSupplyService 类描述：生产请求序号 流水号 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
public interface IRequestSupplyService {

	/**
	 * 
	 * 生成请求序号
	 *
	 *
	 * @Title supplyRequestId
	 * @Description: 生成请求序号
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:13:28
	 */
	String supplyRequestId();

	/**
	 * 
	 * 生成流水号
	 *
	 *
	 * @Title supplySerialNumber
	 * @Description: 生成流水号
	 * @author: caohui
	 * @Date: 2018年5月21日/下午1:13:44
	 */
	String supplySerialNumber();
}
