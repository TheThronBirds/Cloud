package com.yhfin.risk.calculate.service.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.calculate.service.IConsiseCalculateResultService;
import com.yhfin.risk.calculate.service.IEntryConsiseCalculateService;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.calculate.reduce.IInstructionRequestCalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.*;

@Service
public class EntryConsiseCalculateServiceImpl implements IEntryConsiseCalculateService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IInstructionRequestCalculateService calculateService;

    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private IConsiseCalculateResultService resultService;

    @Override
    @HystrixCommand(fallbackMethod = "consiseCalculateFallBack")
    public ServerResponse<String> consiseCalculate(EntryConciseCalculateInfo conciseCalculateInfo) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到计算请求，{}" + conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getRequestId(), JSON.toJSONString(conciseCalculateInfo));
        }
        EntryCalculateResult result = calculateService.calculateRequest(conciseCalculateInfo, conciseCalculateInfo.getFundId());
        result.setCalculateValid(true);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到计算请求，计算唯一标识{},计算结果:{}" + conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getResultKey(), JSON.toJSONString(result));
        }
        CompletableFuture.runAsync(() -> {
            resultService.consiseCalculateResult(result);
        }, executorService);
        return ServerResponse.createBySuccess(conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getSerialNumber());
    }


    public ServerResponse<String> consiseCalculateFallBack(EntryConciseCalculateInfo conciseCalculateInfo, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "计算唯一标识{},计算请求发生错误，" + e.getMessage(), conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getResultKey());
            logger.error("" + e, e);
        }
        EntryCalculateResult result = new EntryCalculateResult(conciseCalculateInfo.getResultKey());
        result.setSerialNumber(conciseCalculateInfo.getSerialNumber());
        result.setRequestId(conciseCalculateInfo.getRequestId());
        result.setFundId(conciseCalculateInfo.getFundId());
        result.setCalculateDetailResult("计算出错:" + e.getMessage());
        result.setCalculateValid(false);
        CompletableFuture.runAsync(() -> {
            resultService.consiseCalculateResult(result);
        }, executorService);
        return ServerResponse.createByError(conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getSerialNumber(), Const.exceptionErrorCode.CALCULATE_ERROR_CODE, e.getMessage());
    }





}
