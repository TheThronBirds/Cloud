/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月17日/上午10:17:42
 * 项目名称: yhfin-risk-cloud-calculate 
 * 文件名称: CaculateResultManageServiceImpl.java
 * 文件描述: @Description 计算结果处理服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.local.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yhfin.risk.cloud.calculate.service.feign.ISendCalculateResultService;
import com.yhfin.risk.cloud.calculate.service.local.ICaculateResultManageService;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 计算结果处理服务 包名称：com.yhfin.risk.cloud.calculate.service.local.impl
 * 类名称：CaculateResultManageServiceImpl 类描述：计算结果处理服务 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@Service
@Slf4j
public class CaculateResultManageServiceImpl implements ICaculateResultManageService {

	@Autowired
	private ISendCalculateResultService sendCalculateResultService;

	/**
	 * 定时线程去队列中拿取数据，失败次数
	 */
	private AtomicInteger invalidTakeNumber = new AtomicInteger();
	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 2,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

	private BlockingDeque<FinalStaticEntryCalculateResultDTO> finalStaticEntryCalculateResultDTOs;
	/**
	 * 定时线程是否启动标识
	 */
	private boolean scheduleStart;
	/**
	 * 定时线程池
	 */
	private ScheduledExecutorService scheduleTakeResult = Executors.newSingleThreadScheduledExecutor();

	{
		this.finalStaticEntryCalculateResultDTOs = new LinkedBlockingDeque<>(20000000);
	}

	int index = 0;

	private synchronized void scheduledForAll() {
		try {
			if (!scheduleStart) {
				this.scheduleStart = true;
				if (scheduleStart) {
					if (log.isInfoEnabled()) {
						log.info("启动定时取结果线程取数据");
					}
					scheduleTakeResult.scheduleAtFixedRate(() -> {
						if (finalStaticEntryCalculateResultDTOs.size() > 0) {
							invalidTakeNumber.set(0);
							List<FinalStaticEntryCalculateResultDTO> calculateResultDTOs = new ArrayList<>(100000);
							finalStaticEntryCalculateResultDTOs.drainTo(calculateResultDTOs, 2000);
							CompletableFuture.runAsync(() -> {
								sendResults(calculateResultDTOs);
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

				}
			}
		} finally {
			scheduleTakeResult = Executors.newSingleThreadScheduledExecutor();
		}

	}

	private void sendResults(List<FinalStaticEntryCalculateResultDTO> calculateResultDTOs) {
		if (calculateResultDTOs != null && !calculateResultDTOs.isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error(
						StringUtil.commonLogStart(calculateResultDTOs.get(0).getSerialNumber(),
								calculateResultDTOs.get(0).getRequestId()) + ",合并{}条发送计算完毕的结果信息到结果处理服务器",
						calculateResultDTOs.size());
			}
			sendCalculateResultService.sendFinalStaticCalculateResults(calculateResultDTOs);
		}
	}

	@Override
	public void putFinalStaticEntryCalculateResultDTOs(FinalStaticEntryCalculateResultDTO calculateResultDTO) {
		try {
			if (!scheduleStart) {
				scheduledForAll();
			}
			finalStaticEntryCalculateResultDTOs.put(calculateResultDTO);
		} catch (InterruptedException e) {
			if (log.isErrorEnabled()) {
				log.error(StringUtil.commonLogStart(calculateResultDTO.getSerialNumber(),
						calculateResultDTO.getRequestId()) + ",接收到一条计算完毕的结果信息");
			}
		}
	}

}
