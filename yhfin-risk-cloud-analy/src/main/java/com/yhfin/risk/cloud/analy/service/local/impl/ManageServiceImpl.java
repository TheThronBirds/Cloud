/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/23:28
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ManageServiceImpl.java
 * 文件描述: @Description 处理计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.local.impl;

import com.yhfin.risk.cloud.analy.service.feign.ISendEntryStaticCalculateService;
import com.yhfin.risk.cloud.analy.service.local.IManageService;
import com.yhfin.risk.core.analy.optimize.IEntryStaticAnalyService;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理计算请求 包名称：com.yhfin.risk.cloud.analy.service.local.impl
 * 类名称：ManageServiceImpl 类描述：处理计算请求 创建人：@author caohui 创建时间：2018/5/13/23:28
 */
@Service
@Slf4j
public class ManageServiceImpl implements IManageService {

	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 2,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

	private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

	private ScheduledExecutorService scheduleTakeResult = Executors.newSingleThreadScheduledExecutor();

	/**
	 * 定时线程去队列中拿取数据，失败次数
	 */
	private AtomicInteger invalidTakeNumber = new AtomicInteger();

	/**
	 * 定时线程是否启动标识
	 */
	private boolean scheduleStart;

	private BlockingDeque<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates;
	{
		this.finalStaticEntryCalculates = new LinkedBlockingDeque<>(20000000);
	}

	@Autowired
	private IEntryStaticAnalyService entryStaticAnalyService;

	@Autowired
	private ISendEntryStaticCalculateService sendEntryStaticCalculateService;
	int index = 0;

	/**
	 * 
	 * 启动定时线程从队列中拿取数据
	 *
	 *
	 * @Title scheduledForAll
	 * @Description: 启动定时线程从队列中拿取数据
	 * @author: caohui
	 * @Date: 2018年5月17日/上午9:56:17
	 */
	private synchronized void scheduledForAll() {
		try {
			if (!scheduleStart) {
				this.scheduleStart = true;
				if (scheduleStart) {
					if (log.isInfoEnabled()) {
						log.info("启动定时取结果线程取数据");
					}
					scheduleTakeResult.scheduleAtFixedRate(() -> {
						if (finalStaticEntryCalculates.size() > 0) {
							invalidTakeNumber.set(0);
							List<FinalStaticEntryCalculateDTO> calculateResultDTOs = new ArrayList<>(8000);
							finalStaticEntryCalculates.drainTo(calculateResultDTOs, 4000);
							CompletableFuture.runAsync(() -> {
								sendFinalStaticEntryCalculates(calculateResultDTOs);
							}, executorService);
						} else {
							invalidTakeNumber.incrementAndGet();
							if (invalidTakeNumber.get() == 30 * 10) {
								scheduleShutdown();
							}
						}
					}, 100, 100, TimeUnit.MILLISECONDS);
				}
			}
			index = 0;
		} catch (Exception e) {
			if (index == 0) {
				index = 1;
				scheduleTakeResult = Executors.newSingleThreadScheduledExecutor();
				scheduledForAll();
			} else {
				if (log.isErrorEnabled()) {
					log.error("定时线程启动失败，基金重试");
				}
			}
		}
	}

	private synchronized void scheduleShutdown() {
		try {
			if (scheduleStart) {
				this.scheduleStart = false;
				if (!scheduleStart) {
					if (log.isInfoEnabled()) {
						log.info("长时间没有结果信息,定时取结果线程停止取数据");
					}
					scheduleTakeResult.shutdown();
					if (log.isInfoEnabled()) {
						log.info("关闭线程成功");
					}

				}
			}
		} finally {
			scheduleTakeResult = Executors.newSingleThreadScheduledExecutor();
		}

	}

	@PostConstruct
	private void init() {
		singleThreadExecutor.execute(() -> {
			while (true) {
				FinalStaticEntryCalculateDTO finalStaticEntryCalculateDTO = entryStaticAnalyService
						.finalStaticEntryCalculateTake();
				if (finalStaticEntryCalculateDTO != null) {
					if (log.isInfoEnabled()) {
						log.info(StringUtil.commonLogStart(
								finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getSerialNumber(),
								finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getRequestId()
										+ ",把计算数据发送给计算服务"));
					}
					try {
						finalStaticEntryCalculates.put(finalStaticEntryCalculateDTO);
						if (!scheduleStart) {
							scheduledForAll();
						}
					} catch (InterruptedException e) {
						if (log.isErrorEnabled()) {
							log.error(StringUtil.commonLogStart(
									finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getSerialNumber(),
									finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getRequestId()
											+ ",把计算数据发送给计算服务失败"));
							log.error("错误原因:" + e, e);
						}
					}
				}
			}
		});

	}

	/**
	 * 发送计算请求
	 *
	 * @param finalStaticEntryCalculates
	 *            计算请求
	 * @return
	 * @throws @Title
	 *             sendFinalStaticEntryCalculates
	 * @Description: 发送计算请求
	 * @author: caohui
	 * @Date: 2018/5/13/23:33
	 */
	@Override
	public ServerResponse<?> sendFinalStaticEntryCalculates(
			List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates) {
		if (finalStaticEntryCalculates != null && !finalStaticEntryCalculates.isEmpty()) {
			String requestId = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult().getRequestId();
			String serialNumber = finalStaticEntryCalculates.get(0).getFinalStaticEntryCalculateResult()
					.getSerialNumber();
			if (log.isInfoEnabled()) {
				log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",合并{}条计算数据发送给计算服务",
						finalStaticEntryCalculates.size());
			}
			return sendEntryStaticCalculateService.consiseCalculates(finalStaticEntryCalculates);
		}
		return ServerResponse.createBySuccess("", "");

	}

	/**
	 * 发送计算请求
	 *
	 * @param finalStaticEntryCalculate
	 *            计算请求
	 * @return
	 * @Title sendFinalStaticEntryCalculates
	 * @Description: 发送计算请求
	 * @author: caohui
	 * @Date: 2018/5/13/23:33
	 */
	@Override
	public ServerResponse<?> sendFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
		return sendEntryStaticCalculateService.consiseCalculate(finalStaticEntryCalculate);
	}

	
	
}
