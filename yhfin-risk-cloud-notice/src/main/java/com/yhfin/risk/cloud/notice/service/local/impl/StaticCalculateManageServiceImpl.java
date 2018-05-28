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
import com.yhfin.risk.cloud.notice.service.feign.ISendStaticSingleFundtCalculateService;
import com.yhfin.risk.cloud.notice.service.local.IStaticCalculateManageService;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticAllFundCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import com.yhfin.risk.core.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IRiskDao;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * 静态风控请求计算管理服务 包名称：com.yhfin.risk.cloud.notice.service.local.impl
 * 类名称：StaticCalculateManageServiceImpl 类描述：静态风控请求计算管理服务 创建人：@author caohui
 * 创建时间：2018/5/13/16:30
 */
@Service
@Slf4j
public class StaticCalculateManageServiceImpl implements IStaticCalculateManageService {

    private volatile StaticAllFundCalculateResultDTO allFundCalculateResultDTO;

    private volatile boolean calculateProcess;

    private volatile String currentSerialNumber;

    @Autowired
    private ISendStaticSingleFundtCalculateService sendStaticSingleFundtCalculateService;

    @Autowired
    private IRiskDao riskDao;

    private ExecutorService handerMessagePool = new ThreadPoolExecutor(1, 1,
            1000L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(20000),
            (runnable) -> {
                return new Thread(runnable, "取出静态基金分析计算状态信息信息线程池单线程");
            });


    private BlockingDeque<HanderMessage<?>> messageDeque;

    {
        this.messageDeque = new LinkedBlockingDeque<>(100000);
    }

    @PostConstruct
    public void init() {
        handerMessagePool.execute(() -> {
            while (true) {
                try {
                    Object message = messageDeque.take().getHanderMessage();
                    if (message != null) {
                        if (message instanceof StaticCalculateDTO) {
                            realHanderStaticCalculate((StaticCalculateDTO) message);
                        } else if (message instanceof ResultHandleResultDTO) {
                            realHanderResultHandleResult((ResultHandleResultDTO) message);
                        } else if (message instanceof SingleFundAnalyResultDTO) {
                            realhHanderSingleFundAnalyResult((SingleFundAnalyResultDTO) message);
                        }
                    }
                } catch (InterruptedException e) {
                    if (log.isErrorEnabled()) {
                        log.error("取出静态基金分析计算状态信息失败", e);
                    }
                }
            }

        });
    }

    @Override
    public void handerResultHandleResult(ResultHandleResultDTO message) {
        try {
            messageDeque.put(new HanderMessage<ResultHandleResultDTO>(message));
        } catch (InterruptedException e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
                        + ",把静态计算基金结果处理信息放入队列中发生错误", e);
            }
        }
    }

    @Override
    public void handerSingleFundAnalyResult(SingleFundAnalyResultDTO message) {
        try {
            messageDeque.put(new HanderMessage<SingleFundAnalyResultDTO>(message));
        } catch (InterruptedException e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
                        + ",把静态计算单个基金分析结果信息放入队列中发生错误", e);
            }
        }
    }

    private void realhHanderSingleFundAnalyResult(SingleFundAnalyResultDTO message) {
        try {
            if (allFundCalculateResultDTO != null) {
                if (StringUtils.equals(message.getSerialNumber(), allFundCalculateResultDTO.getSerialNumber())) {
                    if (!allFundCalculateResultDTO.allFinish()) {
                        allFundCalculateResultDTO.append(message);
                        judgeAllFinish();
                    }
                }

            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
                        + ",处理基金分析结果信息出错,{}", JSON.toJSONString(message));
                log.error("错误原因:", e);
            }
        }
    }

    private void realHanderResultHandleResult(ResultHandleResultDTO message) {
        try {
            if (allFundCalculateResultDTO != null) {
                if (StringUtils.equals(message.getSerialNumber(), allFundCalculateResultDTO.getSerialNumber())) {
                    if (!allFundCalculateResultDTO.allFinish()) {
                        allFundCalculateResultDTO.append(message);
                        judgeAllFinish();
                    }
                }

            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(message.getSerialNumber(), message.getRequestId())
                        + ",处理基金条目计算结果信息出错,{}", JSON.toJSONString(message));
                log.error("错误原因:", e);
            }
        }
    }

    @Override
    public void handerStaticCalculate(StaticCalculateDTO calculate) {
        try {
            messageDeque.put(new HanderMessage<StaticCalculateDTO>(calculate));
        } catch (InterruptedException e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(calculate.getSerialNumber(), calculate.getRequestId())
                        + ",把静态计算请求放入队列中发生错误", e);
            }
        }
    }

    private void realHanderStaticCalculate(StaticCalculateDTO calculate) {
        String requestId = calculate.getRequestId();
        String serialNumber = calculate.getSerialNumber();
        try {
            if (!calculateProcess) {
                if (log.isInfoEnabled()) {
                    log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",引擎开始静态计算请求,{}",
                            JSON.toJSONString(calculate));
                }
                if (calculate.getCalculateAll() != null && calculate.getCalculateAll()) {
                    calculate.setFundIds(riskDao.getAllFundIds());
                } else {
                    List<String> fundIds = calculate.getFundIds();
                    List<String> allFundIds = riskDao.getAllFundIds();
                    if (fundIds == null) {
                        fundIds = new ArrayList<>(800);
                    }

                    if (allFundIds == null) {
                        allFundIds = new ArrayList<>(800);
                    }
                    final List<String> finalAllFundIds = new ArrayList<>(800);
                    finalAllFundIds.addAll(allFundIds);
                    calculate.setFundIds(fundIds.stream().filter((fundId) -> finalAllFundIds.contains(fundId))
                            .collect(Collectors.toList()));

                }
                if (calculate.getFundIds() == null || calculate.getFundIds().isEmpty()) {
                    if (log.isInfoEnabled()) {
                        log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",没有有效的基金发起静态请求计算,计算结束");
                    }
                } else {
                    this.calculateProcess = true;
                    this.currentSerialNumber = serialNumber;
                    if (log.isInfoEnabled()) {
                        log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",开始对有效的基金发起静态请求计算,具体请求计算信息{}",
                                JSON.toJSONString(calculate));
                    }
                    List<String> riskIds = calculate.getRiskIds();

                    List<StaticSingleFundCalculateDTO> calculateSingles = calculate.getFundIds().stream()
                            .map((item) -> {
                                StaticSingleFundCalculateDTO calculateSingle = new StaticSingleFundCalculateDTO();
                                calculateSingle.setFundId(item);
                                calculateSingle.setRiskIds(riskIds);
                                calculateSingle.setRequestId(requestId);
                                calculateSingle.setSerialNumber(serialNumber);
                                return calculateSingle;
                            }).collect(Collectors.toList());
                    allFundCalculateResultDTO = StaticAllFundCalculateResultDTO.createInit(calculateSingles, requestId,
                            serialNumber);
                    for (StaticSingleFundCalculateDTO staticSingleFundCalculate : calculateSingles) {
                        ServerResponse<String> serverResponse = sendStaticSingleFundtCalculateService
                                .staticSingleFundCalculate(staticSingleFundCalculate);
                        StaticSingleFundCalculateResultDTO staticSingleFundCalculateResult = allFundCalculateResultDTO
                                .getFundCalculateResults().get(staticSingleFundCalculate.getFundId());
                        if (serverResponse.isSuccess()) {
                            staticSingleFundCalculateResult.setSendValid(true);
                        } else {
                            staticSingleFundCalculateResult.setSendValid(false);
                            staticSingleFundCalculateResult.setResultValid(true);
                            allFundCalculateResultDTO.getSuccessCalculate().incrementAndGet();
                        }
                    }
                    judgeAllFinish();
                }

            } else {
                if (log.isErrorEnabled()) {
                    log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",引擎正处于计算中,此次请求无效处理,请求{}",
                            JSON.toJSONString(calculate));
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(serialNumber, requestId) + ",处理请求计算出错,", e);
            }
        }
    }

    @Override
    public boolean getCalculateProcess() {
        return this.calculateProcess;
    }

    @Override
    public String getCurrentSerialNumber() {
        return this.currentSerialNumber;
    }

    /**
     * 判断静态计算所有的基金是否全部完成
     *
     * @Title judgeAllFinish
     * @Description: 判断静态计算所有的基金是否全部完成
     * @author: caohui
     * @Date: 2018年5月21日/下午2:26:19
     */
    private void judgeAllFinish() {
        if (allFundCalculateResultDTO != null) {
            if (log.isInfoEnabled()) {
                log.info(
                        StringUtil.commonLogStart(allFundCalculateResultDTO.getSerialNumber(),
                                allFundCalculateResultDTO.getRequestId()) + ",当前已经完成基金数量{},总体基金数量{}",
                        allFundCalculateResultDTO.getSuccessCalculate().get(),
                        allFundCalculateResultDTO.getFundCalculateResults().size());
            }
            allFundCalculateResultDTO.getFundCalculateResults().size();
            if (allFundCalculateResultDTO.allFinish()) {
                if (log.isInfoEnabled()) {
                    log.info(StringUtil.commonLogStart(allFundCalculateResultDTO.getSerialNumber(),
                            allFundCalculateResultDTO.getRequestId()) + ",全部基金处理完毕");
                }
                this.calculateProcess = false;
                this.currentSerialNumber = "";
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("判断是否结束失败,没有所有基金计算阶段结果信息");
            }
            this.calculateProcess = false;
            this.currentSerialNumber = "";
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private class HanderMessage<T> {
        @NonNull
        private T handerMessage;
    }

    @Override
    public void forceFinishStaticCalculate() {
        if (log.isInfoEnabled()) {
            log.info("收到强制结束当前计算请求,开始结算当前计算");
        }
        this.currentSerialNumber = "";
        this.calculateProcess = false;
        this.allFundCalculateResultDTO = null;
    }

    @Override
    public StaticAllFundCalculateResultDTO getStaticAllFundCalculateResult() {

        return allFundCalculateResultDTO;
    }

}
