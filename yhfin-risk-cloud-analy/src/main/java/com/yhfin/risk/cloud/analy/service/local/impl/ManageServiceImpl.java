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
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.springframework.web.client.RestTemplate;

/**
 * 处理计算请求 包名称：com.yhfin.risk.cloud.analy.service.local.impl
 * 类名称：ManageServiceImpl 类描述：处理计算请求 创建人：@author caohui 创建时间：2018/5/13/23:28
 */
@Service
@Slf4j
public class ManageServiceImpl implements IManageService {


	private ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
			new LinkedBlockingQueue<Runnable>(100000), new ThreadPoolExecutor.AbortPolicy());

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);  
	
	@Autowired
	private IEntryStaticAnalyService entryStaticAnalyService;

	@Autowired
	private ISendEntryStaticCalculateService sendEntryStaticCalculateService;

	private List<FinalStaticEntryCalculateDTO> cacheFinalStaticEntryCalculates = new ArrayList<>(10000);

	@PostConstruct
	private void init() {
		singleThreadExecutor.submit(() -> {
			while (true) {
				FinalStaticEntryCalculateDTO finalStaticEntryCalculateDTO = entryStaticAnalyService
						.finalStaticEntryCalculateTake();
				if (finalStaticEntryCalculateDTO != null) {
					CompletableFuture.runAsync(() -> {
						if (log.isInfoEnabled()) {
							log.info(StringUtil.commonLogStart(
									finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getSerialNumber(),
									finalStaticEntryCalculateDTO.getFinalStaticEntryCalculateResult().getRequestId()
											+ ",把计算数据发送给计算服务"));
						}
						sendFinalStaticEntryCalculate(finalStaticEntryCalculateDTO);
					});
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
	public ServerResponse sendFinalStaticEntryCalculates(
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
	public ServerResponse sendFinalStaticEntryCalculate(FinalStaticEntryCalculateDTO finalStaticEntryCalculate) {
		return sendEntryStaticCalculateService.consiseCalculate(finalStaticEntryCalculate);
	}

}
