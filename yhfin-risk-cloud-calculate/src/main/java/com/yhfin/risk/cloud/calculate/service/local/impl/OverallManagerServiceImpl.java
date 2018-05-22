/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:19:19
 * 项目名称: yhfin-risk-cloud-calculate 
 * 文件名称: OverallManagerServiceImpl.java
 * 文件描述: @Description 接收条目同步 内存同步 计算条目统一管理类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.local.impl;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yhfin.risk.cloud.calculate.service.local.ICaculateResultManageService;
import com.yhfin.risk.cloud.calculate.service.local.IOverallManagerService;
import com.yhfin.risk.core.calculate.reduce.ICalculateService;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 接收条目同步 内存同步 计算条目统一管理类 包名称：com.yhfin.risk.cloud.calculate.service.local.impl
 * 类名称：OverallManagerServiceImpl 类描述：接收条目同步 内存同步 计算条目统一管理类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@Service
@Slf4j
public class OverallManagerServiceImpl implements IOverallManagerService {

	private String currentSerialNumber;

	@Autowired
	private ICalculateService calculateService;

	@Autowired
	private IMemorySynchronizateService memorySynchronizateService;

	@Autowired
	private ICaculateResultManageService caculateResultManageService;

	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 3,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(200000), new ThreadPoolExecutor.AbortPolicy());

	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
	private BlockingDeque<HanderMessage<?>> messageDeque;

	{
		this.messageDeque = new LinkedBlockingDeque<>(1000);
	}

	@PostConstruct
	public void init() {
		fixedThreadPool.submit(() -> {
			while (true) {
				Object message = messageDeque.take().getMessage();
				if (message != null) {
					if (message instanceof MemoryMessageSynchronizateDTO) {
						realHanderMemoryMessageSynchronizate((MemoryMessageSynchronizateDTO) message);
					} else if (message instanceof List) {
						realHanderFinalStaticEntryCalculates((List<FinalStaticEntryCalculateDTO>) message);
					} else if (message instanceof FinalStaticEntryCalculateDTO) {
						realHanderFinalStaticEntryCalculate((FinalStaticEntryCalculateDTO) message);
					}
				}
			}
		});
	}

	@Override
	public void handerMemoryMessageSynchronizate(MemoryMessageSynchronizateDTO memoryMessageSynchronizate) {
		String serialNumber = memoryMessageSynchronizate.getSerialNumber();
		String requestId = memoryMessageSynchronizate.getRequestId();
		try {
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到同步内存消息,把消息放入队列中,等待处理");
			}
			messageDeque.put(new HanderMessage<MemoryMessageSynchronizateDTO>(memoryMessageSynchronizate));
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",收到同步内存消息,把消息放入队列中,等待处理出现错误", e);
			}
		}
	}

	private void realHanderMemoryMessageSynchronizate(MemoryMessageSynchronizateDTO message) {
		try {
			if (message.getSynchrozateAll() != null && message.getSynchrozateAll()) {
				if (message.getDropTable() != null && message.getDropTable()) {
					memorySynchronizateService.synchronizateAllTableDatas();
				}
				return;
			} else {
				List<String> tableNames = message.getTableNames();
				if (tableNames != null && !tableNames.isEmpty()) {
					if (message.getDropTable()) {
						memorySynchronizateService
								.synchronizateTableDatas(tableNames.toArray(new String[tableNames.size()]));
					}
					memorySynchronizateService
							.synchronizateDropTables(tableNames.toArray(new String[tableNames.size()]));
				}
			}

		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
						+ ",同步内存消息处理失败,同步内存版本号{}", message.getMemoryVersionNumber());
				log.error("错误原因:" + e, e);
			}
		}
	}

	@Override
	public void handerFinalStaticEntryCalculates(List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates) {
		if (finalStaticEntryCalculates != null && !finalStaticEntryCalculates.isEmpty()) {
			String serialNumber = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult()
					.getSerialNumber();
			String requestId = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getRequestId();
			try {
				if (log.isInfoEnabled()) {
					log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到{}条初始计算消息,把消息放入队列中,等待处理",
							finalStaticEntryCalculates.size());
				}
				messageDeque.put(new HanderMessage<List<FinalStaticEntryCalculateDTO>>(finalStaticEntryCalculates));
			} catch (InterruptedException e) {
				if (log.isErrorEnabled()) {
					log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",收到初始计算消息,把消息放入队列中,等待处理出现错误", e);
				}
			}
		}

	}

	private void realHanderFinalStaticEntryCalculates(List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates) {
		for (FinalStaticEntryCalculateDTO calculateDTO : finalStaticEntryCalculates) {
			realHanderFinalStaticEntryCalculate(calculateDTO);
		}
	}

	@Override
	public void handerFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
		if (finalStaticEntryCalculate != null) {
			String serialNumber = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber();
			String requestId = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getRequestId();
			try {
				if (log.isInfoEnabled()) {
					log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到初始计算消息,把消息放入队列中,等待处理");
				}
				messageDeque.put(new HanderMessage<FinalStaticEntryCalculateDTO>(finalStaticEntryCalculate));
			} catch (InterruptedException e) {
				if (log.isErrorEnabled()) {
					log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",收到初始计算消息,把消息放入队列中,等待处理出现错误", e);
				}
			}
		}

	}

	private void realHanderFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
		if (StringUtils.isBlank(this.currentSerialNumber)) {
			this.currentSerialNumber = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber();
			CompletableFuture.runAsync(() -> {
				FinalStaticEntryCalculateResultDTO calculateResult = calculateService
						.calculateRequest(finalStaticEntryCalculate);
				caculateResultManageService.putFinalStaticEntryCalculateResultDTOs(calculateResult);
			}, executorService);
		} else {
			if (StringUtils.equals(this.currentSerialNumber,
					finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber())) {
				CompletableFuture.runAsync(() -> {
					FinalStaticEntryCalculateResultDTO calculateResult = calculateService
							.calculateRequest(finalStaticEntryCalculate);
					caculateResultManageService.putFinalStaticEntryCalculateResultDTOs(calculateResult);
				}, executorService);
			} else {
				if (Integer.valueOf(this.currentSerialNumber).compareTo(Integer.valueOf(
						finalStaticEntryCalculate.getFinalStaticEntryCalculateResult().getSerialNumber())) < 0) {
					this.currentSerialNumber = finalStaticEntryCalculate.getFinalStaticEntryCalculateResult()
							.getSerialNumber();
					caculateResultManageService.getBlockingDeque().clear();
					CompletableFuture.runAsync(() -> {
						FinalStaticEntryCalculateResultDTO calculateResult = calculateService
								.calculateRequest(finalStaticEntryCalculate);
						caculateResultManageService.putFinalStaticEntryCalculateResultDTOs(calculateResult);
					}, executorService);
				}
			}

		}

	}

	@Override
	public String getCurrentSerialNumber() {

		return this.currentSerialNumber;
	}

	/**
	 * 
	 * 处理消息 包名称：com.yhfin.risk.cloud.calculate.service.local.impl
	 * 类名称：HanderMessage 类描述：处理消息 创建人：@author caohui 创建时间：2018/5/13/12:20
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@RequiredArgsConstructor
	private class HanderMessage<T> {
		@NonNull
		private T message;
	}

}
