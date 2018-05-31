/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午1:11:32
 * 项目名称: yhfin-risk-cloud-notice 
 * 文件名称: RequestSupplyServiceImpl.java
 * 文件描述: @Description 生成请求序号 流水号
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yhfin.risk.cloud.notice.service.local.IRequestSupplyService;
import com.yhfin.risk.core.dao.IRiskDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 生成请求序号 流水号 包名称：com.yhfin.risk.cloud.notice.service.local.impl
 * 类名称：RequestSupplyServiceImpl 类描述：生成请求序号 流水号 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@Slf4j
@Service
public class RequestSupplyServiceImpl implements IRequestSupplyService {

	@Autowired
	private IRiskDao riskDao;

	@Override
	public String supplyRequestId() {
		String result = "";
		result = UUID.randomUUID().toString();
		if (log.isDebugEnabled()) {
			log.debug("生成UUID{},当作请求序号", result);
		}
		return result;
	}

	@Override
	public String supplySerialNumber() {
		String result = "";
		result = riskDao.getNextSerialNumber();
		if (log.isDebugEnabled()) {
			log.debug("生成下个流水号{}", result);
		}
		return result;
	}

}
