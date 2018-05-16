/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:30
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: StaticCalculateManageServiceImpl.java
 * 文件描述: @Description 静态风控请求计算管理服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local.impl;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.notice.service.feign.ISendMessageService;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.execeptions.YhRiskNoticeException;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateNoticeDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 静态风控请求计算管理服务 包名称：com.yhfin.risk.cloud.notice.service.local.impl
 * 类名称：StaticCalculateManageServiceImpl 类描述：静态风控请求计算管理服务 创建人：@author caohui
 * 创建时间：2018/5/13/16:30
 */
@Service
@Slf4j
public class StaticCalculateManageServiceImpl implements IStaticCalculateManageService {

	@Autowired
	private ISendMessageService sendMessageService;
	private ConcurrentHashMap<String, StaticSingleFundCalculateResultDTO> fundCalculateResults = new ConcurrentHashMap<>(
			400);

	private AtomicInteger successCalculate = new AtomicInteger();
	/**
	 * 是否计算中
	 */
	private boolean calculateProcess;
	/**
	 * 计算中的版本号
	 */
	private String currentSerialNumber;

	/**
	 * 初始化静态计算请求
	 *
	 * @param calculateResult
	 *            计算分析结果
	 * @Title initStaticManage
	 * @Description: 初始化静态计算请求
	 * @author: caohui
	 * @Date: 2018/5/13/22:47
	 */
	@Override
	public synchronized void initStaticManage(List<StaticSingleFundCalculateDTO> staticSingleFundCalculateRequests,
			String requestId, String serialNumber) {

		if (calculateProcess) {
			throw new YhRiskNoticeException("风控引擎处于计算中");
		}
		calculateProcess = true;
		this.currentSerialNumber = serialNumber;
		fundCalculateResults.clear();

		for (int i = 0; i < staticSingleFundCalculateRequests.size(); i++) {
			StaticSingleFundCalculateDTO staticSingleFundCalculate = staticSingleFundCalculateRequests.get(i);
			fundCalculateResults.put(staticSingleFundCalculate.getFundId(),
					initStaticSingleFundCalculateResultBySuccess(staticSingleFundCalculate.getFundId(),
							staticSingleFundCalculate.getRequestId(), staticSingleFundCalculate.getSerialNumber()));
		}

	}

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
	@Override
	public synchronized void hander(ResultHandleResultDTO message) {
		if (calculateProcess && StringUtils.equals(message.getSerialNumber(), this.currentSerialNumber)) {
			StaticSingleFundCalculateResultDTO staticSingleFundCalculateResultDTO = fundCalculateResults
					.get(message.getFundId());
			staticSingleFundCalculateResultDTO.append(message);
			if (staticSingleFundCalculateResultDTO.getAnalyValid()) {
				if (log.isInfoEnabled()) {
					log.info(
							StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",基金{},分析完毕",
							message.getFundId());
				}
			}
			if (staticSingleFundCalculateResultDTO.getResultValid()) {
				if (log.isInfoEnabled()) {
					log.info(
							StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",基金{},处理完毕",
							message.getFundId());
				}
				successCalculate.incrementAndGet();
				judgeSuccessFinish(message.getRequestId(), message.getSerialNumber());
			}
		}
	}

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
	@Override
	public synchronized void hander(SingleFundAnalyResultDTO message) {
		if (log.isInfoEnabled()) {
			log.info(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",开始处理基金分析结果消息");
			log.info("当前通知服务器工作状态:" + calculateProcess);
			log.info("当前通知服务器流水号:" + this.currentSerialNumber);
		}
		if (calculateProcess && StringUtils.equals(message.getSerialNumber(), this.currentSerialNumber)) {
			if (log.isInfoEnabled()) {
				log.info("开始获取当前通知中心,基金{},状态信息", message.getFundId());
			}
			StaticSingleFundCalculateResultDTO staticSingleFundCalculateResultDTO = fundCalculateResults
					.get(message.getFundId());
			if (log.isInfoEnabled()) {
				log.info("处理前基金{},通知服务器基金状态,{}", message.getFundId(),
						JSON.toJSONString(staticSingleFundCalculateResultDTO));
			}
			staticSingleFundCalculateResultDTO.append(message);
			if (log.isInfoEnabled()) {
				log.info("处理后基金{},通知服务器基金状态,{}", message.getFundId(),
						JSON.toJSONString(staticSingleFundCalculateResultDTO));
			}
			if (staticSingleFundCalculateResultDTO.getResultValid()) {
				if (log.isInfoEnabled()) {
					log.info(
							StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId()) + ",基金{},处理完毕",
							message.getFundId());
				}
				successCalculate.incrementAndGet();
				judgeSuccessFinish(message.getRequestId(), message.getSerialNumber());
			}
		}
	}

	private void judgeSuccessFinish(String requestId, String serialNumber) {
		
		if(log.isInfoEnabled()){
			log.info("判断静态请求是否处理完毕");
		}
		
		if(log.isInfoEnabled()){
			log.info("需要处理的基金数量{}",fundCalculateResults.size());
			log.info("成功静态计算完毕基金数量{}",successCalculate.get());
		}
		if (successCalculate.get() == fundCalculateResults.size()) {
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",所有基金处理完毕");
			}
			StaticCalculateNoticeDTO staticCalculateNotice = new StaticCalculateNoticeDTO();
			this.calculateProcess = false;
			this.currentSerialNumber = "";
			this.fundCalculateResults.clear();
			this.successCalculate = new AtomicInteger();
			staticCalculateNotice.setFinish(true);
			staticCalculateNotice.setRequestId(requestId);
			staticCalculateNotice.setSerialNumber(serialNumber);
			sendMessageService.staticCalculateMessageSynchronizate(staticCalculateNotice);
		}
	}

	/**
	 * 
	 * 初始化静态通知基金计算状态
	 *
	 *
	 * @Title initStaticSingleFundCalculateResultBySuccess
	 * @Description: 初始化静态通知基金计算状态
	 * @author: caohui
	 * @Date: 2018年5月14日/下午4:57:55
	 */
	private StaticSingleFundCalculateResultDTO initStaticSingleFundCalculateResultBySuccess(String fundId,
			String requestId, String serialNumber) {
		StaticSingleFundCalculateResultDTO result = new StaticSingleFundCalculateResultDTO();
		result.setFundId(fundId);
		result.setRequestId(requestId);
		result.setSerialNumber(serialNumber);
		result.setSendValid(false);
		result.setAnalyValid(false);
		result.setResultValid(false);
		return result;
	}

	@Override
	public synchronized void hander(String fundId, boolean sendValid, String requestId, String serialNumber) {
		StaticSingleFundCalculateResultDTO staticSingleFundCalculateResultDTO = fundCalculateResults.get(fundId);
		staticSingleFundCalculateResultDTO.setSendValid(sendValid);
		if (!sendValid) {
			successCalculate.incrementAndGet();
			staticSingleFundCalculateResultDTO.setResultValid(true);
		}
		judgeSuccessFinish(requestId, serialNumber);
	}

	@Override
	public void initNoticeState() {
		StaticCalculateNoticeDTO staticCalculateNotice = new StaticCalculateNoticeDTO();
		staticCalculateNotice.setFinish(true);
		staticCalculateNotice.setRequestId("");
		staticCalculateNotice.setSerialNumber(this.currentSerialNumber);
		sendMessageService.staticCalculateMessageSynchronizate(staticCalculateNotice);
		this.calculateProcess = false;
		this.currentSerialNumber = "";
	}

}
