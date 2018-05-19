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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

/**
 * 静态风控请求计算管理服务 
 * 包名称：com.yhfin.risk.cloud.notice.service.local.impl
 * 类名称：StaticCalculateManageServiceImpl 
 * 类描述：静态风控请求计算管理服务 
 * 创建人：@author caohui
 * 创建时间：2018/5/13/16:30
 */
@Service
@Slf4j
public class StaticCalculateManageServiceImpl implements IStaticCalculateManageService {

	@Autowired
	private ISendMessageService sendMessageService;

	private BlockingDeque<HanderStaticCalculateState<?>> handerStaticCalculateStates;

	{
		this.handerStaticCalculateStates = new LinkedBlockingDeque<>(100000);
	}

	private ExecutorService staticMessageExecutor = Executors.newSingleThreadExecutor();

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

	@PostConstruct
	public void init() {
		staticMessageExecutor.submit(() -> {
			while (true) {
				HanderStaticCalculateState<?> staticCalculateStateMessage = handerStaticCalculateStates.take();
				handerMessage(staticCalculateStateMessage);
			}
		});
	}

	/**
	 * 
	 * 处理静态计算消息
	 *
	 *
	 * @Title handerMessage
	 * @Description: 处理静态计算消息
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:08:58
	 */
	private void handerMessage(HanderStaticCalculateState<?> message) {
		Object handerMessage = message.getMessage();
		if (handerMessage instanceof StaticCalculateNoticeDTO) {
			realInitNoticeState((StaticCalculateNoticeDTO) handerMessage);
		} else if (handerMessage instanceof FundSendState) {
			realHanderFundSendState((FundSendState) handerMessage);
		} else if (handerMessage instanceof SingleFundAnalyResultDTO) {
			realHanderSingleFundAnalyResult((SingleFundAnalyResultDTO) handerMessage);
		} else if (handerMessage instanceof ResultHandleResultDTO) {
			realHanderResultHandleResultDTO((ResultHandleResultDTO) handerMessage);
		}
	}

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
		HanderStaticCalculateState<ResultHandleResultDTO> handerStaticCalculateState = new HanderStaticCalculateState<>();
		handerStaticCalculateState.setMessage(message);
		handerStaticCalculateState.setRequestId(message.getRequestId());
		handerStaticCalculateState.setSerialNumber(message.getSerialNumber());
		try {
			handerStaticCalculateStates.put(handerStaticCalculateState);
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error("基金计算结果消息处理失败", e);
			}
			throw new YhRiskNoticeException("基金计算结果消息处理失败" + e);
		}

	}

	/**
	 * 
	 * 真正处理结果消息
	 *
	 *
	 * @Title realHanderResultHandleResultDTO
	 * @Description: 真正处理结果消息
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:42:17
	 */
	private void realHanderResultHandleResultDTO(ResultHandleResultDTO message) {
		if (calculateProcess && StringUtils.equals(message.getSerialNumber(), this.currentSerialNumber)) {
			StaticSingleFundCalculateResultDTO staticSingleFundCalculateResultDTO = fundCalculateResults
					.get(message.getFundId());
			if (staticSingleFundCalculateResultDTO == null) {
				return;
			}
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
	public void hander(SingleFundAnalyResultDTO message) {
		HanderStaticCalculateState<SingleFundAnalyResultDTO> handerStaticCalculateState = new HanderStaticCalculateState<>();
		handerStaticCalculateState.setMessage(message);
		handerStaticCalculateState.setRequestId(message.getRequestId());
		handerStaticCalculateState.setSerialNumber(message.getSerialNumber());
		try {
			handerStaticCalculateStates.put(handerStaticCalculateState);
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error("基金分析结果处理失败", e);
			}
			throw new YhRiskNoticeException("基金分析结果处理失败" + e);
		}
	}

	/**
	 * 
	 * 真正处理基金分析结果请求
	 *
	 *
	 * @Title realHanderSingleFundAnalyResult
	 * @Description: 真正处理基金分析结果请求
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:35:46
	 */
	private void realHanderSingleFundAnalyResult(SingleFundAnalyResultDTO message) {
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
			if (staticSingleFundCalculateResultDTO == null) {
				return;
			}
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

	/**
	 * 
	 * 判断所有基金是否全部处理成功
	 *
	 *
	 * @Title judgeSuccessFinish
	 * @Description: 判断所有基金是否全部处理成功
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:33:42
	 */
	private void judgeSuccessFinish(String requestId, String serialNumber) {
		if (log.isInfoEnabled()) {
			log.info("判断静态请求是否处理完毕");
		}
		if (log.isInfoEnabled()) {
			log.info("需要处理的基金数量{}", fundCalculateResults.size());
			log.info("成功静态计算完毕基金数量{}", successCalculate.get());
		}
		if (successCalculate.get() == fundCalculateResults.size()) {
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",所有基金处理完毕");
			}
			StaticCalculateNoticeDTO staticCalculateNotice = new StaticCalculateNoticeDTO();
			this.calculateProcess = false;
			this.currentSerialNumber = "";
			this.fundCalculateResults.clear();
			this.successCalculate.set(0);
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
	public void hander(String fundId, boolean sendValid, String requestId, String serialNumber) {
		FundSendState fundSendState = new FundSendState();
		fundSendState.setRequestId(requestId);
		fundSendState.setSerialNumber(serialNumber);
		fundSendState.setSendValid(sendValid);
		fundSendState.setFundId(fundId);
		HanderStaticCalculateState<FundSendState> handerStaticCalculateState = new HanderStaticCalculateState<>();
		handerStaticCalculateState.setRequestId(requestId);
		handerStaticCalculateState.setSerialNumber(serialNumber);
		handerStaticCalculateState.setMessage(fundSendState);
		try {
			handerStaticCalculateStates.put(handerStaticCalculateState);
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error("基金发送结果消息处理失败", e);
			}
			throw new YhRiskNoticeException("基金发送结果消息处理失败" + e);
		}

	}

	/**
	 * 
	 * 真正处理基金发送结果消息
	 *
	 *
	 * @Title realHanderFundSendState
	 * @Description: 真正处理基金发送结果消息
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:25:00
	 */
	private void realHanderFundSendState(FundSendState fundSendState) {
		if (this.calculateProcess && StringUtils.equals(this.currentSerialNumber, fundSendState.getSerialNumber())) {
			StaticSingleFundCalculateResultDTO staticSingleFundCalculateResultDTO = fundCalculateResults
					.get(fundSendState.getFundId());
			if (staticSingleFundCalculateResultDTO != null) {
				boolean sendValid = fundSendState.isSendValid();
				staticSingleFundCalculateResultDTO.setSendValid(sendValid);
				if (!sendValid) {
					successCalculate.incrementAndGet();
					staticSingleFundCalculateResultDTO.setResultValid(true);
					judgeSuccessFinish(fundSendState.getRequestId(), fundSendState.getSerialNumber());
				}
			}
		}
	}

	@Override
	public void initNoticeState() {
		StaticCalculateNoticeDTO staticCalculateNotice = new StaticCalculateNoticeDTO();
		staticCalculateNotice.setFinish(true);
		staticCalculateNotice.setRequestId(this.currentSerialNumber);
		staticCalculateNotice.setSerialNumber(this.currentSerialNumber);
		HanderStaticCalculateState<StaticCalculateNoticeDTO> handerStaticCalculateState = new HanderStaticCalculateState<>();
		handerStaticCalculateState.setMessage(staticCalculateNotice);
		try {
			handerStaticCalculateStates.put(handerStaticCalculateState);
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error("初始化通知中心出错", e);
			}
			throw new YhRiskNoticeException("初始化通知中心出错" + e);
		}
	}

	/**
	 * 
	 * 实际处理通知服务器初始化状态消息
	 *
	 *
	 * @Title realInitNoticeState
	 * @Description: 实际处理通知服务器初始化状态消息
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:18:04
	 */
	private void realInitNoticeState(StaticCalculateNoticeDTO staticCalculateNotice) {
		if (staticCalculateNotice.getFinish()) {
			this.calculateProcess = false;
			this.currentSerialNumber = "";
			this.successCalculate.set(0);
			this.fundCalculateResults.clear();
			sendMessageService.staticCalculateMessageSynchronizate(staticCalculateNotice);
		}
	}

	/**
	 * 
	 * 静态风控结果处理消息 
	 * 包名称：com.yhfin.risk.cloud.notice.service.local.impl
	 * 类名称：HanderStaticCalculateState 
	 * 类描述：静态风控结果处理消息 
	 * 创建人：@author caohui
	 * 创建时间：2018/5/13/12:20
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	private class HanderStaticCalculateState<T> {
		private String requestId;
		private String serialNumber;
		private T message;
	}

	/**
	 * 
	 * 基金发送状态 
	 * 包名称：com.yhfin.risk.cloud.notice.service.local.impl
	 * 类名称：FundSendState 
	 * 类描述：基金发送状态 
	 * 创建人：@author caohui 
	 * 创建时间：2018/5/13/12:20
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	private class FundSendState {
		private String fundId;
		private String requestId;
		private String serialNumber;
		private boolean sendValid;
	}

}
