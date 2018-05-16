/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:08
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: HanderResultServiceImpl.java
 * 文件描述: @Description 结果处理
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.result.service.local.impl;

import com.yhfin.risk.cloud.result.service.feign.ISendMessageService;
import com.yhfin.risk.cloud.result.service.local.IHanderResultService;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IRiskDao;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 结果处理 包名称：com.yhfin.risk.cloud.result.service.local.impl
 * 类名称：HanderResultServiceImpl 类描述：结果处理 创建人：@author caohui 创建时间：2018/5/14/0:08
 */
@Service
@Slf4j
public class HanderResultServiceImpl implements IHanderResultService {

	@Autowired
	private ISendMessageService messageService;

	/**
	 * 当前流水号
	 */
	private String currentSerialNumber;
	private Connection connection;
	private ExecutorService executorService = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors() * 2,
			0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

	private BlockingDeque<FinalStaticEntryCalculateResultDTO> finalStaticEntryCalculateResultDTOs;

	{
		this.finalStaticEntryCalculateResultDTOs = new LinkedBlockingDeque<>(20000000);
	}

	@Value("${yhfin.data.risk.url}")
	private String url;
	@Value("${yhfin.data.risk.username}")
	private String username;
	@Value("${yhfin.data.risk.password}")
	private String password;

	@PostConstruct
	private void init() {
		initConnection();
	}

	private void initConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
			Class.forName("oracle.jdbc.OracleDriver");
			this.connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			if (log.isErrorEnabled()) {
				log.error("获取连接失败," + e, e);
			}
		}
	}

	@Scheduled(fixedDelay = 100)
	public void scheduledForAll() {
		if (finalStaticEntryCalculateResultDTOs.size() > 0) {
			List<FinalStaticEntryCalculateResultDTO> calculateResultDTOs = new ArrayList<>(100000);
			finalStaticEntryCalculateResultDTOs.drainTo(calculateResultDTOs, 10000);
			CompletableFuture.runAsync(() -> {
				forAllCalculateResultInfo(calculateResultDTOs);
			}, executorService);
		}
	}

	/**
	 * 处理计算结果
	 *
	 * @param calculateResultDTOList
	 *            结果列表
	 * @return
	 * @Title handerResult
	 * @Description: 处理计算结果
	 * @author: caohui
	 * @Date: 2018/5/14/0:06
	 */
	@Override
	public void handerResults(List<FinalStaticEntryCalculateResultDTO> calculateResultDTOList) {
		if (calculateResultDTOList != null && !calculateResultDTOList.isEmpty()) {
			finalStaticEntryCalculateResultDTOs.addAll(calculateResultDTOList);
		}
	}

	private void forAllCalculateResultInfo(List<FinalStaticEntryCalculateResultDTO> calculateResults) {
		if (calculateResults == null || calculateResults.isEmpty()) {
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("开始向数据库存放计算结果信息");
		}
		CallableStatement cstmt = null;
		try {
			StructDescriptor recDesc = StructDescriptor.createDescriptor("RISKRESULT_STATIC_BASE_OBJ", this.connection);
			ArrayList<STRUCT> pstruct = new ArrayList<STRUCT>();
			for (FinalStaticEntryCalculateResultDTO result : calculateResults) {
				Object[] objs = new Object[15];
				objs[0] = result.getResultId();
				objs[1] = Integer.valueOf(result.getRiskId());
				objs[2] = result.getRiskDescription();
				objs[3] = 0;
				objs[4] = 0;
				objs[5] = result.getDeclareType();
				objs[6] = result.getResultUnit();
				objs[7] = result.getCmpareBs();
				objs[8] = Integer.valueOf(result.getFundId());
				objs[9] = result.getVcStockCode();
				objs[10] = result.getOffendType();
				objs[11] = result.getEnValue();
				objs[12] = result.getEnResultValue();
				objs[13] = result.getVcFzValue();
				objs[14] = result.getVcFmValue();
				STRUCT struct = new STRUCT(recDesc, this.connection, objs);
				pstruct.add(struct);
			}
			ArrayDescriptor tabDesc = ArrayDescriptor.createDescriptor("STATIC_BASE_ARRAY", this.connection);
			ARRAY vArray = new ARRAY(tabDesc, this.connection, pstruct.toArray());
			cstmt = this.connection.prepareCall("call FORALL_STATIC_BASE(?,?,?)");
			cstmt.setArray(1, vArray);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.registerOutParameter(3, Types.VARCHAR);
			cstmt.execute();
			Integer resultCode = cstmt.getInt(2);
			if (resultCode == 0) {
				String message = cstmt.getString(3);
				if (StringUtils.isNotBlank(message)) {
					if (log.isErrorEnabled()) {
						log.error("批量插入计算结果基本最终信息失败，" + message);
					}
				}
				CompletableFuture.runAsync(() -> {
					sendMeaage(calculateResults, false);
				}, executorService);
				return;
			}
			if (log.isInfoEnabled()) {
				log.info("批量插入{}条计算结果最终信息成功", calculateResults.size());
			}
			CompletableFuture.runAsync(() -> {
				sendMeaage(calculateResults, true);
			}, executorService);

		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("批量插入计算结果最终信息失败，" + e, e);
			}
			CompletableFuture.runAsync(() -> {
				sendMeaage(calculateResults, false);
			}, executorService);
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (SQLException e) {
				if (log.isErrorEnabled()) {
					log.error("关闭CallableStatement失败，" + e);
				}
			}
		}
	}

	private void sendMeaage(List<FinalStaticEntryCalculateResultDTO> calculateResults, boolean valid) {
		if (calculateResults != null && !calculateResults.isEmpty()) {
			if (log.isInfoEnabled()) {
				log.info("发送计算结果处理信息给消息服务器");
			}
			String requestId = calculateResults.get(0).getRequestId();
			String serialNumber = calculateResults.get(0).getSerialNumber();
			Map<String, List<FinalStaticEntryCalculateResultDTO>> listMap = calculateResults.stream()
					.collect(Collectors.groupingBy(FinalStaticEntryCalculateResultDTO::getFundId));
			listMap.values().parallelStream().map((item) -> {
				if (item.size() > 0) {
					ResultHandleResultDTO resultHandleResultDTO = new ResultHandleResultDTO();
					if (valid) {
						resultHandleResultDTO.setSuccessResult(new AtomicInteger(item.size()));
					} else {
						resultHandleResultDTO.setErrorResult(new AtomicInteger(item.size()));
					}
					resultHandleResultDTO.setRequestId(requestId);
					resultHandleResultDTO.setSerialNumber(serialNumber);
					resultHandleResultDTO.setFundId(item.get(0).getFundId());
					return resultHandleResultDTO;
				}
				return null;

			}).filter(item -> item != null).forEach(messageService::resultMessage);
		}
	}

	@Override
	public void handerResult(FinalStaticEntryCalculateResultDTO calculateResult) {
		if (calculateResult != null) {
			try {
				if (log.isInfoEnabled()) {
					log.info(
							StringUtil.commonLogStart(calculateResult.getSerialNumber(), calculateResult.getRequestId())
									+ ",缓存一条计算结果信息");
				}
				finalStaticEntryCalculateResultDTOs.put(calculateResult);
			} catch (InterruptedException e) {
				if (log.isErrorEnabled()) {
					log.error(
							StringUtil.commonLogStart(calculateResult.getSerialNumber(), calculateResult.getRequestId())
									+ ",存放计算结果到范围中,发生错误");
					log.error("错误:", e);
				}
			}
		}
	}
}
