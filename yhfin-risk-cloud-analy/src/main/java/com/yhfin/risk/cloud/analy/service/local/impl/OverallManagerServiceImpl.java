/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月21日/下午4:17:15
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: OverallManagerServiceImpl.java
 * 文件描述: @Description 全局管理同步内存 同步条目 接收计算请求管理类
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local.impl;

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

import com.yhfin.risk.cloud.analy.service.local.IOverallManagerService;
import com.yhfin.risk.core.analy.manage.IEntryStaticAnalyManageService;
import com.yhfin.risk.core.analy.optimize.IEntryStaticAnalyService;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.EntryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.pojos.dtos.synchronizate.MemoryMessageSynchronizateDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import com.yhfin.risk.core.synchronizate.entry.IEntrySynchronizateService;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局管理同步内存 同步条目 接收计算请求管理类 包名称：com.yhfin.risk.cloud.analy.service.local.impl
 * 类名称：OverallManagerServiceImpl 类描述：全局管理同步内存 同步条目 接收计算请求管理类 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@Service
@Slf4j
public class OverallManagerServiceImpl implements IOverallManagerService {

	private String currentSerialNumber;

	@Autowired
	private IMemorySynchronizateService memorySynchronizateService;

	@Autowired
	private IEntrySynchronizateService entrySynchronizateService;

	@Autowired
	private IEntryStaticAnalyManageService entryStaticAnalyManageService;

	@Autowired
	private IEntryStaticAnalyService entryStaticAnalyService;

	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

	private BlockingDeque<HanderNoticeMessage<?>> messageDeque;

	{
		this.messageDeque = new LinkedBlockingDeque<>(1000);
	}

	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 2,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(20000), new ThreadPoolExecutor.AbortPolicy());

	@PostConstruct
	public void init() {
		fixedThreadPool.submit(() -> {
			while (true) {
				Object message = messageDeque.take().getNoticeMessage();
				if (message != null) {
					if (message instanceof EntryMessageSynchronizateDTO) {
						realHanderEntryMessageSynchronizate((EntryMessageSynchronizateDTO) message);
					} else if (message instanceof MemoryMessageSynchronizateDTO) {
						realHanderMemoryMessageSynchronizate((MemoryMessageSynchronizateDTO) message);
					} else if (message instanceof StaticSingleFundCalculateDTO) {
						realHanderStaticSingleFundCalculate((StaticSingleFundCalculateDTO) message);
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
			messageDeque.put(new HanderNoticeMessage<MemoryMessageSynchronizateDTO>(memoryMessageSynchronizate));
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
	public void handerEntryMessageSynchronizate(EntryMessageSynchronizateDTO entryMessageSynchronizate) {
		String serialNumber = entryMessageSynchronizate.getSerialNumber();
		String requestId = entryMessageSynchronizate.getRequestId();
		try {
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到同步条目消息,把消息放入队列中,等待处理");
			}
			messageDeque.put(new HanderNoticeMessage<EntryMessageSynchronizateDTO>(entryMessageSynchronizate));
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",收到同步条目消息,把消息放入队列中,等待处理出现错误", e);
			}
		}
	}

	private void realHanderEntryMessageSynchronizate(EntryMessageSynchronizateDTO message) {
		try {
			if (message.getSynchronizateAll() != null && message.getSynchronizateAll()) {
				entrySynchronizateService.updateAllEntrys();
				return;
			}
			List<String> deleteRiskIds = message.getDeleteRiskIds();
			if (deleteRiskIds != null && !deleteRiskIds.isEmpty()) {
				entrySynchronizateService.deleteEntrys(deleteRiskIds.toArray(new String[deleteRiskIds.size()]));
			}
			List<String> updateRiskIds = message.getUpdateRiskIds();
			if (updateRiskIds != null && !updateRiskIds.isEmpty()) {
				entrySynchronizateService.updateEntrys(updateRiskIds.toArray(new String[updateRiskIds.size()]));
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
						+ ",同步条目消息处理失败,同步条目版本号{}", message.getEntryVersionNumber());
				log.error("错误原因:" + e, e);
			}
		}

	}

	@Override
	public void handerStaticSingleFundCalculate(StaticSingleFundCalculateDTO singleFundCalculate) {
		String serialNumber = singleFundCalculate.getSerialNumber();
		String requestId = singleFundCalculate.getRequestId();
		try {
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",收到静态基金分析计算消息,把消息放入队列中,等待处理");
			}
			messageDeque.put(new HanderNoticeMessage<StaticSingleFundCalculateDTO>(singleFundCalculate));
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",收到静态基金分析计算消息,把消息放入队列中,等待处理出现错误", e);
			}
		}
	}

	private void realHanderStaticSingleFundCalculate(StaticSingleFundCalculateDTO singleFundCalculate) {
		if (singleFundCalculate != null) {
			if (StringUtils.isBlank(this.currentSerialNumber)) {
				this.currentSerialNumber = singleFundCalculate.getSerialNumber();
				entryStaticAnalyManageService.getfundAnalyResults().clear();
				CompletableFuture.runAsync(() -> {
					if (singleFundCalculate.getRiskIds() == null || singleFundCalculate.getFundId().isEmpty()) {
						entryStaticAnalyService.staticCalculateRequestSingleFund(singleFundCalculate.getFundId(),
								singleFundCalculate.getRequestId(), singleFundCalculate.getSerialNumber(), null);
					} else {
						entryStaticAnalyService.staticCalculateRequestSingleFund(singleFundCalculate.getFundId(),
								singleFundCalculate.getRequestId(), singleFundCalculate.getSerialNumber(),
								singleFundCalculate.getRiskIds()
										.toArray(new String[singleFundCalculate.getRiskIds().size()]));
					}

				}, executorService);

			} else {
				if (StringUtils.equals(this.currentSerialNumber, singleFundCalculate.getSerialNumber())) {
					CompletableFuture.runAsync(() -> {
						if (singleFundCalculate.getRiskIds() == null || singleFundCalculate.getFundId().isEmpty()) {
							entryStaticAnalyService.staticCalculateRequestSingleFund(singleFundCalculate.getFundId(),
									singleFundCalculate.getRequestId(), singleFundCalculate.getSerialNumber(), null);
						} else {
							entryStaticAnalyService.staticCalculateRequestSingleFund(singleFundCalculate.getFundId(),
									singleFundCalculate.getRequestId(), singleFundCalculate.getSerialNumber(),
									singleFundCalculate.getRiskIds()
											.toArray(new String[singleFundCalculate.getRiskIds().size()]));
						}

					}, executorService);
				} else {
					if (Integer.valueOf(this.currentSerialNumber)
							.compareTo(Integer.valueOf(singleFundCalculate.getSerialNumber())) < 0) {
						this.currentSerialNumber = singleFundCalculate.getSerialNumber();
						entryStaticAnalyManageService.getfundAnalyResults().clear();
						CompletableFuture.runAsync(() -> {
							if (singleFundCalculate.getRiskIds() == null || singleFundCalculate.getFundId().isEmpty()) {
								entryStaticAnalyService.staticCalculateRequestSingleFund(
										singleFundCalculate.getFundId(), singleFundCalculate.getRequestId(),
										singleFundCalculate.getSerialNumber(), null);
							} else {
								entryStaticAnalyService.staticCalculateRequestSingleFund(
										singleFundCalculate.getFundId(), singleFundCalculate.getRequestId(),
										singleFundCalculate.getSerialNumber(), singleFundCalculate.getRiskIds()
												.toArray(new String[singleFundCalculate.getRiskIds().size()]));
							}

						}, executorService);
					}
				}
			}
		}
	}

	/**
	 * 
	 * 接收通知中心的消息的包装类 包名称：com.yhfin.risk.cloud.analy.service.local.impl
	 * 类名称：HanderNoticeMessage 类描述：接收通知中心的消息的包装类 创建人：@author caohui
	 * 创建时间：2018/5/13/12:20
	 */
	@Setter
	@Getter
	@RequiredArgsConstructor
	private class HanderNoticeMessage<T> {
		@NonNull
		private T noticeMessage;
	}

	@Override
	public String getCurrentSerialNumber() {

		return this.currentSerialNumber;
	}

}
